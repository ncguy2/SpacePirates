package net.game.spacepirates.particles.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import net.game.spacepirates.particles.*;
import net.game.spacepirates.services.Services;
import net.game.spacepirates.util.ComputeShader;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;

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

    protected ParticleProfile profile;
    protected ParticleBuffer buffer;
    protected Supplier<Matrix3> transformSupplier;
    protected ParticleShader spawnShader;
    protected ParticleShader updateShader;
    protected float life;

    protected ShaderStorageBufferObject indexBuffer;
    protected ShaderStorageBufferObject newIndexBuffer;

    public AbstractParticleSystem(ParticleProfile profile, ParticleBuffer buffer) {
        this.profile = profile;
        this.buffer = buffer;

        indexBuffer = new ShaderStorageBufferObject(profile.particleCount * Integer.BYTES);
        newIndexBuffer = new ShaderStorageBufferObject(profile.particleCount * Integer.BYTES);

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
            particleService.getParticleBlock(block).ifPresent(this::addBlock);
        }

        spawnShader.reloadImmediate();
        updateShader.reloadImmediate();
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

        ByteBuffer b = ByteBuffer.allocate(particleIndices.size() * Integer.BYTES);
        particleIndices.forEach(b::putInt);
        b.position(0);
//        indexBuffer.setData(b);

        if(indexBuffer != null) {
            indexBuffer.dispose();
            indexBuffer = null;
        }

        indexBuffer = new ShaderStorageBufferObject(b, 2);
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
    }

    public abstract boolean canStillSpawn();

    public int spawnParticles(int amount) {
        int[] indices = fetchParticleIndices(amount);
        if (indices.length == 0) {
            // No free indices in buffer
            return 0;
        }

        ComputeShader program = spawnShader.program();
        program.bind();

        ByteBuffer b = ByteBuffer.allocate(indices.length * Integer.BYTES);
        IntStream.of(indices).forEach(b::putInt);
        b.position(0);
        newIndexBuffer.setData(b);

        program.bindSSBO(3, newIndexBuffer);

        setCommonUniforms(indices, program, Gdx.graphics.getDeltaTime());

        program.dispatch(amount);
        program.waitForCompletion();
        program.unbind();

        return indices.length;
    }

    public void updateParticles(float delta) {
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

        uniformSetters.forEach(program::setUniform);

        program.bindSSBO(0, buffer.getParticleBuffer());
        program.bindSSBO(1, buffer.getDeadBuffer());
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

    }

    public void reset() {
        life = 0;
    }
}
