package net.game.spacepirates.particles.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import net.game.spacepirates.data.messaging.MessageBus;
import net.game.spacepirates.particles.*;
import net.game.spacepirates.services.Services;
import net.game.spacepirates.util.ArrayUtils;
import net.game.spacepirates.util.ComputeShader;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;
import net.game.spacepirates.util.curve.Curve;
import net.game.spacepirates.util.curve.GLColourCurve;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public abstract class AbstractParticleSystem {

    protected final Map<String, Consumer<Integer>> uniformSetters;
    protected final List<Consumer<ShaderProgram>> uniformTasks;
    protected final Map<String, String> macroParams;

    protected boolean spawnerEnabled = true;
    protected boolean simulationEnabled = true;
    protected boolean isFinishing = false;

    protected boolean isFinished = false;

    protected ParticleProfile profile;
    protected ParticleBuffer buffer;
    protected Supplier<Matrix3> transformSupplier;
    protected ParticleShader spawnShader;
    protected ParticleShader updateShader;
    protected float life;
    protected int loopCounter = 0;

    protected ShaderStorageBufferObject indexBuffer;

    public AbstractParticleSystem(ParticleProfile profile, ParticleBuffer buffer) {
        this.profile = profile;
        this.buffer = buffer;

        indexBuffer = new ShaderStorageBufferObject(profile.particleCount * Integer.BYTES);
        indexBuffer.init();

        this.uniformSetters = new HashMap<>();
        this.uniformTasks = new ArrayList<>();
        this.macroParams = new HashMap<>();

        FileHandle fileHandle = Gdx.files.internal("particles/skeleton.comp");

        macroParams.put("p_type", "IS_SPAWN");
        spawnShader = new ParticleShader(profile.name + ", Spawn shader", fileHandle, macroParams);
        macroParams.put("p_type", "IS_UPDATE");
        updateShader = new ParticleShader(profile.name + ", Update shader", fileHandle, macroParams);

        ParticleService particleService = Services.get(ParticleService.class);
        for (String block : profile.blocks) {
            Optional<ParticleBlock> particleBlock = particleService.getParticleBlock(block);
            if(particleBlock.isPresent()) {
                this.addBlock(particleBlock.get());
            }else{
                System.err.println("No particle block registered with name: " + block);
            }
        }

        spawnShader.reloadImmediate();
        updateShader.reloadImmediate();
    }

    public boolean isSpawnerEnabled() {
        return spawnerEnabled;
    }

    public AbstractParticleSystem setSpawnerEnabled(boolean spawnerEnabled) {
        this.spawnerEnabled = spawnerEnabled;
        return this;
    }

    public boolean isSimulationEnabled() {
        return simulationEnabled;
    }

    public AbstractParticleSystem setSimulationEnabled(boolean simulationEnabled) {
        this.simulationEnabled = simulationEnabled;
        return this;
    }

    public void addUniform(String uniform, Consumer<Integer> setter) {
        uniformSetters.put(uniform, setter);
    }

    public void addBlock(ParticleBlock block) {
        addBlock(block, block.type);
    }

    public ShaderStorageBufferObject getIndexBuffer() {
        updateIndexBuffer();
        return indexBuffer;
    }

    protected void updateIndexBuffer() {
        List<Integer> particleIndices = Services.get(ParticleService.class).getInts(this);

        ByteBuffer b = ByteBuffer.allocateDirect(particleIndices.size() * Integer.BYTES);
        particleIndices.stream().sorted().forEach(i -> {
            byte[] bytes = ArrayUtils.intToGLArr(i);
            b.put(bytes);
        });
        b.position(0);
        indexBuffer.setData(b);
    }

    public int[] fetchParticleIndices(int amount) {
        return Services.get(ParticleService.class).issueIndices(amount, this);
    }

    public int[] getIndices() {
        return Services.get(ParticleService.class)
                       .getInts(this)
                       .stream()
                       .mapToInt(i -> i) // Map to IntStream
                       .toArray();
    }

    public void updateSystem(float delta) {
        updateIndexBuffer();
        if(isFinishing) {
            checkFinishState();
        }
    }

    protected void checkFinishState() {
        int[] indices = getIndices();
        if(indices.length > 0) {
            // Still simulating
            return;
        }

        setSimulationEnabled(false);
        isFinished = true;
        MessageBus.get().dispatch(ParticleService.MessageTopics.SYSTEM_FINISHED, this);
    }

    public abstract boolean canStillSpawn();

    public int spawnParticles(int amount) {

        if(!isSpawnerEnabled()) {
            return 0;
        }

        int[] indices = fetchParticleIndices(amount);
        if (indices.length == 0) {
            // No free indices in buffer
            return 0;
        }

        ComputeShader program = spawnShader.program();
        program.bind();

        ByteBuffer b = ByteBuffer.allocateDirect(indices.length * Integer.BYTES);
        IntStream.of(indices).forEach(i -> {
            byte[] bytes = ArrayUtils.intToGLArr(i);
            b.put(bytes);
        });
        b.position(0);
        indexBuffer.setData(b);

        program.bindSSBO(2, indexBuffer);

        setCommonUniforms(indices, program, Gdx.graphics.getDeltaTime());
        setSpawnUniforms(program);

        program.dispatch(amount);
        program.waitForCompletion();
        program.unbind();

        return indices.length;
    }

    public void updateParticles(float delta) {

        if(!isSimulationEnabled()) {
            return;
        }

        life += delta;

        int[] indices = getIndices();
        if (indices.length == 0) {
            // No indices allocated to system

            if(!canStillSpawn()) {
                beginFinishPhase();
            }

            return;
        }

        ComputeShader program = updateShader.program();

        program.bind();
        setCommonUniforms(Services.get(ParticleService.class).getInts(this).stream().mapToInt(i -> i).toArray(), program, delta);
        setUpdateUniforms(program);
        program.bindSSBO(2, getIndexBuffer());

        program.dispatch(indices.length);
        program.unbind();
    }

    public void setCommonUniforms(int[] indices, ComputeShader program, float delta) {
        // Transform Matrix
        program.setUniform("u_transform", loc -> {
            Matrix3 mat;
            if (transformSupplier != null) {
                mat = transformSupplier.get();
            } else {
                mat = new Matrix3();
            }
            Gdx.gl.glUniformMatrix3fv(loc, 1, false, mat.getValues(), 0);
        });

        program.setUniform("u_particleCount", loc -> Gdx.gl.glUniform1i(loc, indices.length));

        // Delta time
        program.setUniform("u_delta", loc -> Gdx.gl.glUniform1f(loc, delta));
        // Time
        program.setUniform("u_globalTime", loc -> Gdx.gl.glUniform1f(loc, Services.get(ParticleService.class).getGlobalLife()));
        // RNG seed
        program.setUniform("u_rngBaseSeed", loc -> Gdx.gl.glUniform1i(loc, ThreadLocalRandom.current().nextInt()));

        if(profile.curve != null && !profile.curve.items.isEmpty()) {
            bind("u_curve", profile.curve);
        }

        uniformSetters.forEach(program::setUniform);

        program.bindSSBO(0, buffer.getParticleBuffer());
        program.bindSSBO(1, buffer.getDeadBuffer());

        buffer.getDeadBufferCounter().bind(5);
    }

    public void setSpawnUniforms(ComputeShader program) {}
    public void setUpdateUniforms(ComputeShader program) {}

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

    public void setTransformSupplier(Supplier<Matrix3> transformSupplier) {
        this.transformSupplier = transformSupplier;
    }

    protected void addBlock(ParticleBlock block, ParticleBlock.Type type) {
        switch (type) {
            case Spawn:
                spawnShader.addBlock(block);
                break;
            case Update:
                updateShader.addBlock(block);
                break;
        }
    }

    public void beginFinishPhase() {
        switch (profile.loopingBehaviour) {
            case None:
                doFinish();
                return;
            case Amount:
                if(loopCounter >= profile.loopingAmount) {
                    doFinish();
                }else{
                    nextLoop();
                }
                return;
            case Forever:
                nextLoop();
                return;
        }
    }

    public <T extends AbstractParticleSystem> void as(Class<T> type, Consumer<T> task) {
        if(type.isInstance(this)) {
            task.accept(type.cast(this));
        }
    }

    public void doFinish() {
        isFinishing = true;
        setSpawnerEnabled(false);
        setSimulationEnabled(true);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void nextLoop() {
        int c = loopCounter;
        reset();
        loopCounter = c + 1;
    }

    public void reset() {
        loopCounter = 0;
        life = 0;
    }
}
