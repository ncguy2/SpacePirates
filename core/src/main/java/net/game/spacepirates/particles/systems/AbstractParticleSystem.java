package net.game.spacepirates.particles.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import net.game.spacepirates.particles.ParticleBlock;
import net.game.spacepirates.particles.ParticleManager;
import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.particles.ParticleShader;
import net.game.spacepirates.util.ComputeShader;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;
import net.game.spacepirates.util.curve.Curve;
import net.game.spacepirates.util.curve.GLColourCurve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractParticleSystem {

    public static final int INVOCATIONS_PER_WORKGROUP = 16;
    public static final int PARTICLE_BYTES = 72;
    public static float GlobalLife = 0;
    public final Map<String, Consumer<Integer>> uniformSetters;
    public final List<Consumer<ShaderProgram>> uniformTasks;
    public final Map<String, String> macroParams;
    private final ParticleProfile profile;
    public Supplier<Matrix3> spawnMatrixSupplier;
    public ParticleRenderData renderData = new ParticleRenderData();
    public int bufferId;
    public int desiredAmount;
    public Runnable onFinish;
    public Runnable onLoop;
    protected float life;
    public float duration;
    protected LoopingBehaviour loopingBehaviour = LoopingBehaviour.None;
    protected int loopingAmount = 1;
    protected ParticleShader spawnScript;
    protected ParticleShader updateScript;
    protected ShaderStorageBufferObject particleBuffer;

    public AbstractParticleSystem(ParticleProfile profile) {
        this.profile = profile;
        macroParams = new HashMap<>();
        this.duration = profile.duration;
        this.desiredAmount = profile.particleCount;
        uniformSetters = new HashMap<>();
        uniformTasks = new ArrayList<>();
        life = 0;
        loopingBehaviour = profile.loopingBehaviour;
        loopingAmount = profile.loopingAmount;

        FileHandle fileHandle = Gdx.files.internal("particles/compute/skeleton.comp");

        ParticleManager.get().addSystem(this);

        macroParams.put("p_BindingPoint", String.valueOf(bufferId));

        spawnScript = new ParticleShader("Particle spawn script", fileHandle, macroParams);
        updateScript = new ParticleShader("Particle update script", fileHandle, macroParams);

        for (String block : profile.blocks) {
            ParticleManager.get()
                    .getParticleBlock(block)
                    .ifPresent(this::addBlock);
        }

        spawnScript.reloadImmediate();
        updateScript.reloadImmediate();

        setAmount(desiredAmount);

        bindBuffer();
    }

    public void addBlock(ParticleBlock block) {
        addBlock(block, block.type);
    }

    public void addBlock(ParticleBlock block, ParticleBlock.Type type) {
        switch(type) {
            case Spawn: spawnScript.addBlock(block); break;
            case Update: updateScript.addBlock(block); break;
        }
    }

    public void setAmount(float fAmount) {
        setAmount(Math.round(fAmount));
    }

    public void setAmount(int amount) {
        if(particleBuffer != null) {
            particleBuffer.unbind();
            particleBuffer.dispose();
        }

        amount = round(amount, 256);
        desiredAmount = amount;

        particleBuffer = new ShaderStorageBufferObject(desiredAmount * PARTICLE_BYTES);

        bindBuffer();
    }

    public float getLifePercent() {
        return life / duration;
    }

    public void addUniform(String name, Consumer<Integer> task) {
        uniformSetters.put(name, task);
    }

    public void reset() {
        life = 0;
    }

    public int round(double i, int v) {
        return (int) (Math.round(i / v) * v);
    }

    public int spawn(int offset, int amount) {
        ComputeShader program = spawnScript.program();
        program.bind();
        program.setUniform("u_spawnMatrix", loc -> {
            Matrix3 p;
            if(spawnMatrixSupplier == null) {
                p = new Matrix3();
            }else{
                p = spawnMatrixSupplier.get();
            }
            Gdx.gl.glUniformMatrix3fv(loc, 1, false, p.getValues(), 0);
        });

        program.setUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.setUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));

        program.setUniform("u_startId", loc -> Gdx.gl.glUniform1i(loc, offset));
        program.setUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, ThreadLocalRandom.current().nextInt()));
        program.setUniform("iMaxParticleCount", loc -> Gdx.gl.glUniform1i(loc, desiredAmount));

        uniformSetters.forEach(program::setUniform);

        bindBuffer();

        int amtSpawned = round(amount, INVOCATIONS_PER_WORKGROUP) / INVOCATIONS_PER_WORKGROUP;
        program.dispatch(amtSpawned);
        program.unbind();
        return amtSpawned;
    }

    public void bindBuffer() {
        bindBuffer(bufferId);
    }

    public void bindBuffer(int location) {
        if(particleBuffer == null) {
            return;
        }

        try{
            spawnScript.setParticleBuffer(location, particleBuffer);
            updateScript.setParticleBuffer(location, particleBuffer);
            particleBuffer.bind(location);
        }catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        if(profile.curve != null) {
            bind("u_curve", profile.curve);
        }
    }

    public void update(float delta) {
        life += delta;

        ComputeShader program = updateScript.program();
        program.bind();
        bindBuffer();

        program.setUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, ThreadLocalRandom.current().nextInt()));
        program.setUniform("u_delta", loc -> Gdx.gl.glUniform1f(loc, delta));
        program.setUniform("iTime", loc -> Gdx.gl.glUniform1f(loc, life));
        program.setUniform("gTime", loc -> Gdx.gl.glUniform1f(loc, GlobalLife));
        program.setUniform("imaxParticleCount", loc -> Gdx.gl.glUniform1i(loc, desiredAmount));
        program.setUniform("u_noiseScale", loc -> Gdx.gl.glUniform1f(loc, 0.001f));
        program.setUniform("u_vectorFieldIntensity", loc -> Gdx.gl.glUniform1f(loc, 1.5f));
        uniformSetters.forEach(program::setUniform);

        int amtSpawned = round(desiredAmount, INVOCATIONS_PER_WORKGROUP) / INVOCATIONS_PER_WORKGROUP;
        program.dispatch(amtSpawned);
        program.unbind();

        if (shouldFinish()) {
            finish();
        }
    }

    public boolean shouldFinish() {
        if(duration < 0) {
            return false;
        }

        return life >= duration;
    }

    public void beginFinish() {
        loopingBehaviour = LoopingBehaviour.None;
    }

    public void finish() {
        finish(false);
    }

    public void finish(boolean force) {
        if(!force && loopingBehaviour.equals(LoopingBehaviour.Forever) || (loopingBehaviour.equals(LoopingBehaviour.Amount) && loopingAmount > 0)) {
            if(loopingBehaviour.equals(LoopingBehaviour.Amount)) {
                loopingAmount--;
            }
            if(onLoop != null) {
                onLoop.run();
            }
            reset();
            return;
        }

        if(onFinish != null) {
            onFinish.run();
        }

        ParticleManager.get().removeSystem(this, () -> {
            if(spawnScript != null) {
                spawnScript.shutdown();
            }
            if(updateScript != null) {
                updateScript.shutdown();
            }

            spawnScript = null;
            updateScript = null;
        });
    }

    public boolean isFinished() {
        return spawnScript == null;
    }

    public void bind(String uniform, GLColourCurve curve) {
        List<Curve.Item<Color>> items = curve.items;
        addUniform(uniform + ".Length", l -> Gdx.gl.glUniform1i(l, items.size()));
        for (int i = 0; i < items.size(); i++) {
            Curve.Item<Color> col = items.get(i);
            String prefix = uniform + ".Entries[" + i + "]";
            addUniform(prefix + ".Key", l -> Gdx.gl.glUniform1f(l, col.value));
            addUniform(prefix + ".Value", l -> Gdx.gl.glUniform4f(l, col.item.r, col.item.g, col.item.b, col.item.a));
        }
    }

    public static enum SystemType {
        Burst(BurstParticleSystem.class, BurstParticleSystem::new),
        Temporal(TemporalParticleSystem.class, TemporalParticleSystem::new),
        TexturedBurst(TexturedBurstParticleSystem.class, TexturedBurstParticleSystem::new),
        TexturedTemporal(TexturedTemporalParticleSystem.class, TexturedTemporalParticleSystem::new),
        ;
        public final Class<? extends AbstractParticleSystem> type;
        private final Function<ParticleProfile, AbstractParticleSystem> builder;

        SystemType(Class<? extends AbstractParticleSystem> type, Function<ParticleProfile, AbstractParticleSystem> builder) {
            this.type = type;
            this.builder = builder;
        }

        public boolean is(AbstractParticleSystem system) {
            if (system == null)
                return false;
            return type.isInstance(system);
        }

        public AbstractParticleSystem create(ParticleProfile profile) {
            return builder.apply(profile);
        }
    }

    public static enum LoopingBehaviour {
        None,
        Amount,
        Forever
    }

}
