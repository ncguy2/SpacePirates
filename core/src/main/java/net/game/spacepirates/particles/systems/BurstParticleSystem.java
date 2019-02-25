package net.game.spacepirates.particles.systems;

import net.game.spacepirates.particles.ParticleProfile;

public class BurstParticleSystem extends AbstractParticleSystem {

    protected boolean hasSpawned = false;

    public BurstParticleSystem(ParticleProfile profile) {
        super(profile);
    }

    @Override
    public void update(float delta) {
        if(!hasSpawned) {
            spawn(0, desiredAmount);
            hasSpawned = true;
        }
        super.update(delta);
    }

    @Override
    public void reset() {
        super.reset();
        hasSpawned = false;
    }
}
