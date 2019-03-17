package net.game.spacepirates.particles.system;

import net.game.spacepirates.particles.ParticleBuffer;
import net.game.spacepirates.particles.ParticleProfile;

public class TemporalParticleSystem extends AbstractParticleSystem {

    protected int amtSpawned = 0;

    public TemporalParticleSystem(ParticleProfile profile, ParticleBuffer buffer) {
        super(profile, buffer);
    }

    @Override
    public void updateSystem(float delta) {
        super.updateSystem(delta);
        float factor = life / profile.spawnOverTime;


        int toSpawn = (int) Math.ceil(profile.particleCount * factor);
        toSpawn -= amtSpawned;

        if(toSpawn > 0) {
            amtSpawned += spawnParticles(toSpawn);
        }
        updateParticles(delta);
    }

    @Override
    public boolean canStillSpawn() {
        float factor = life / profile.spawnOverTime;
        int toSpawn = (int) Math.ceil(profile.particleCount * factor);
        toSpawn -= amtSpawned;
        return toSpawn > 0;
    }
}
