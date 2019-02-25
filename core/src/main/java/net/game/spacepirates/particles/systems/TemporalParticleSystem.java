package net.game.spacepirates.particles.systems;

import net.game.spacepirates.particles.ParticleProfile;

public class TemporalParticleSystem extends AbstractParticleSystem {

    protected float spawnOverTime;
    protected int amtSpawned = 0;

    public TemporalParticleSystem(ParticleProfile profile) {
        super(profile);
        this.spawnOverTime = profile.spawnOverTime;
    }

    @Override
    public void update(float delta) {

        float factor = life / spawnOverTime;
        int toSpawn = (int) Math.ceil(desiredAmount * factor);
        toSpawn -= amtSpawned;

        if(toSpawn > 0) {
            amtSpawned += spawn(amtSpawned, toSpawn);
        }

        super.update(delta);
    }

    @Override
    public void beginFinish() {
        super.beginFinish();
        amtSpawned = desiredAmount;
    }

    @Override
    public void reset() {
        super.reset();
        amtSpawned = 0;
    }
}
