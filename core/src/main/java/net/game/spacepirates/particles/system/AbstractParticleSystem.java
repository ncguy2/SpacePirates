package net.game.spacepirates.particles.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import net.game.spacepirates.particles.*;
import net.game.spacepirates.services.Services;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AbstractParticleSystem {

    protected final Map<String, Consumer<Integer>> uniformSetters;
    protected final List<Consumer<ShaderProgram>> uniformTasks;
    protected final Map<String, String> macroParams;

    protected ParticleProfile profile;
    protected ParticleBuffer buffer;
    protected List<Integer> particleIndices;
    protected Supplier<Matrix3> spawnMatrixSupplier;
    protected ParticleShader spawnShader;
    protected ParticleShader updateShader;

    public AbstractParticleSystem(ParticleProfile profile, ParticleBuffer buffer) {
        this.profile = profile;
        this.buffer = buffer;
        this.particleIndices = new ArrayList<>();

        this.uniformSetters = new HashMap<>();
        this.uniformTasks = new ArrayList<>();
        this.macroParams = new HashMap<>();

        FileHandle fileHandle = Gdx.files.internal("particles/skeleton.comp");

        spawnShader = new ParticleShader(profile.name + ", Spawn shader", fileHandle, macroParams);
        updateShader = new ParticleShader(profile.name + ", Update shader", fileHandle, macroParams);

        ParticleService particleService = Services.get(ParticleService.class);
        for (String block : profile.blocks) {
            particleService.getParticleBlock(block).ifPresent(this::addBlock);
        }

        spawnShader.reloadImmediate();
        updateShader.reloadImmediate();
    }

    public void addBlock(ParticleBlock block) {
        addBlock(block, block.type);
    }

    protected void addBlock(ParticleBlock block, ParticleBlock.Type type) {
        switch(type) {
            case Spawn:  spawnShader.addBlock(block);  break;
            case Update: updateShader.addBlock(block); break;
        }
    }

    public void setParticleIndices(int... indices) {
        Arrays.stream(indices).forEach(particleIndices::add);
    }

    public void killParticles(int... indices) {
        Arrays.stream(indices).forEach(particleIndices::remove);
    }

    public void fetchParticleIndices(int amount) {
        setParticleIndices(Services.get(ParticleService.class).issueIndices(amount));
    }

    public int[] getIndices() {
        return particleIndices.stream()
                              .mapToInt(i -> i) // Map to IntStream
                              .toArray();
    }

    public void update(float delta) {
        /*
        int[] indices = buffer.getIndices()
        shader.setUniform("u_indices", indices);
         */
    }

}
