package net.game.spacepirates.entity.component;

import com.badlogic.gdx.Gdx;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.particles.ParticleService;
import net.game.spacepirates.particles.system.AbstractParticleSystem;
import net.game.spacepirates.services.Services;

import java.util.Optional;
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


    @Override
    public void update(float delta) {
        if (profile == null || !profile.name.equalsIgnoreCase(systemName)) {
            reinit();
        }

        super.update(delta);

        Gdx.app.postRunnable(() -> system.updateSystem(delta));
    }

    public void reinit() {
        Gdx.app.postRunnable(this::reinitImmediate);
    }

    public void reinitImmediate() {
        if(system != null) {
            if(onFinish != null) {
                onFinish.accept(this);
            }

            system.beginFinishPhase();
            system = null;
        }

        system = buildSystem();

        if(system != null) {
            system.setTransformSupplier(transform::worldTransform);

            if(onInit != null) {
                onInit.accept(this, system);
            }
        }
    }

    public AbstractParticleSystem buildSystem() {
        ParticleService particleService = Services.get(ParticleService.class);
        Optional<ParticleProfile> profile = particleService.getProfile(systemName);

        if(!profile.isPresent()) {
            return null;
        }

        this.profile = profile.get();

        AbstractParticleSystem abstractParticleSystem = particleService.buildSystem(this.profile).orElse(null);

        if(abstractParticleSystem == null) {
            return null;
        }

        abstractParticleSystem.setTransformSupplier(this.transform::worldTransform);
        return abstractParticleSystem;
    }

    @Override
    public void onRemoveFromEntity(Entity entity) {
        if(system != null) {
            system.beginFinishPhase();
            system = null;
        }
        super.onRemoveFromEntity(entity);
    }
}
