package net.game.spacepirates.particles.system;

import net.game.spacepirates.particles.ParticleBuffer;
import net.game.spacepirates.particles.ParticleProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractParticleSystem {

    private ParticleProfile profile;
    private ParticleBuffer buffer;
    private List<Integer> particleIndices;

    public AbstractParticleSystem(ParticleProfile profile, ParticleBuffer buffer) {
        this.profile = profile;
        this.buffer = buffer;
        this.particleIndices = new ArrayList<>();
    }

    public void setParticleIndices(int... indices) {
        Arrays.stream(indices).forEach(particleIndices::add);
    }

    public void killParticles(int... indices) {
        Arrays.stream(indices).forEach(particleIndices::remove);
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
