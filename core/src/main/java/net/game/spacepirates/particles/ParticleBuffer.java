package net.game.spacepirates.particles;

import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;

public class ParticleBuffer {

    public static final int PARTICLE_SIZE = 64;
    private ShaderStorageBufferObject particleBuffer;
    private ShaderStorageBufferObject deadBuffer;

    public ParticleBuffer(int particleCount) {
        particleBuffer = new ShaderStorageBufferObject(PARTICLE_SIZE * particleCount);
        deadBuffer = new ShaderStorageBufferObject(Integer.BYTES + (particleCount * Integer.BYTES));
    }

    public ShaderStorageBufferObject getParticleBuffer() {
        return particleBuffer;
    }

    public ShaderStorageBufferObject getDeadBuffer() {
        return deadBuffer;
    }
}
