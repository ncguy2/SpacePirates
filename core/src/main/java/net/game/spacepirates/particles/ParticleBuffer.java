package net.game.spacepirates.particles;

import net.game.spacepirates.util.buffer.AtomicBuffer;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;

public class ParticleBuffer {

    public static final int PARTICLE_SIZE = 64;
    private ShaderStorageBufferObject particleBuffer;
    private ShaderStorageBufferObject deadBuffer;
    private AtomicBuffer deadBufferCounter;

    public ParticleBuffer(int particleCount) {
        particleBuffer = new ShaderStorageBufferObject(PARTICLE_SIZE * particleCount);
        deadBuffer = new ShaderStorageBufferObject(particleCount * Integer.BYTES);
        deadBufferCounter = new AtomicBuffer();

        particleBuffer.init();
        deadBuffer.init();
        deadBufferCounter.init();
    }

    public ShaderStorageBufferObject getParticleBuffer() {
        return particleBuffer;
    }

    public ShaderStorageBufferObject getDeadBuffer() {
        return deadBuffer;
    }

    public AtomicBuffer getDeadBufferCounter() {
        return deadBufferCounter;
    }
}
