package net.game.spacepirates.particles.system;

import net.game.spacepirates.particles.ParticleBuffer;
import net.game.spacepirates.particles.ParticleProfile;

public class BurstParticleSystem extends AbstractParticleSystem {

    boolean hasSpawned = false;

    public BurstParticleSystem(ParticleProfile profile, ParticleBuffer buffer) {
        super(profile, buffer);
    }

    @Override
    public void updateSystem(float delta) {

        super.updateSystem(delta);

        if(!hasSpawned) {
            spawnParticles(profile.particleCount);
            hasSpawned = true;
        }

        updateParticles(delta);
    }

    @Override
    public boolean canStillSpawn() {
        return !hasSpawned;
    }

}
