package net.game.spacepirates.particles;

import net.game.spacepirates.particles.system.AbstractParticleSystem;

import java.util.function.Function;

public enum SystemType {
//    Burst(BurstParticleSystem.class, BurstParticleSystem::new),
//    Temporal(TemporalParticleSystem.class, TemporalParticleSystem::new),
//    TexturedBurst(TexturedBurstParticleSystem.class, TexturedBurstParticleSystem::new),
//    TexturedTemporal(TexturedTemporalParticleSystem.class, TexturedTemporalParticleSystem::new),
    ;

    public final Class<? extends AbstractParticleSystem> type;
    private final Function<ParticleProfile, AbstractParticleSystem> builder;

    SystemType(Class<? extends AbstractParticleSystem> type, Function<ParticleProfile, AbstractParticleSystem> builder) {
        this.type = type;
        this.builder = builder;
    }

    public boolean is(AbstractParticleSystem system) {
        if (system == null)
            return false;
        return type.isInstance(system);
    }

    public AbstractParticleSystem create(ParticleProfile profile) {
        return builder.apply(profile);
    }
}

