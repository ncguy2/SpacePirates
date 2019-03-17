package net.game.spacepirates.particles.system;

import net.game.spacepirates.particles.ParticleBuffer;
import net.game.spacepirates.particles.ParticleProfile;

import java.util.function.BiFunction;

public enum ParticleSystemType {
    Burst(BurstParticleSystem.class, BurstParticleSystem::new),
    Temporal(TemporalParticleSystem.class, TemporalParticleSystem::new),
//    TexturedBurst(TexturedBurstParticleSystem.class, TexturedBurstParticleSystem::new),
//    TexturedTemporal(TexturedTemporalParticleSystem.class, TexturedTemporalParticleSystem::new),
    ;

    public final Class<? extends AbstractParticleSystem> type;
    private final BiFunction<ParticleProfile, ParticleBuffer, AbstractParticleSystem> builder;

    ParticleSystemType(Class<? extends AbstractParticleSystem> type, BiFunction<ParticleProfile, ParticleBuffer, AbstractParticleSystem> builder) {
        this.type = type;
        this.builder = builder;
    }

    public boolean is(AbstractParticleSystem system) {
        if (system == null)
            return false;
        return type.isInstance(system);
    }

    public AbstractParticleSystem create(ParticleProfile profile, ParticleBuffer buffer) {
        return builder.apply(profile, buffer);
    }
}

