package net.game.spacepirates.system;

import net.game.spacepirates.particles.ParticleManager;
import net.game.spacepirates.world.GameWorld;

public class ParticleSystem extends AbstractSystem {

    public ParticleSystem(GameWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void startup() {

    }

    @Override
    public void update(float delta) {
        ParticleManager.get().update(delta);
    }

    @Override
    public void shutdown() {

    }
}
