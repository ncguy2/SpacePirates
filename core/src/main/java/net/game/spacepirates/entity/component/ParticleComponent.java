package net.game.spacepirates.entity.component;

import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.particles.system.AbstractParticleSystem;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

// TODO update to support new GPU particles
public class ParticleComponent extends SceneComponent<ParticleComponent> {

    public ParticleComponent(String name) {
        super(name);
    }

    public String systemName;

    public transient ParticleProfile profile;
    public transient AbstractParticleSystem system;

    public BiConsumer<ParticleComponent, AbstractParticleSystem> onInit;
    public Consumer<ParticleComponent> onFinish;

}
