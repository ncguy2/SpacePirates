package net.game.spacepirates.entity.component;

import com.badlogic.gdx.Gdx;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.particles.ParticleManager;
import net.game.spacepirates.particles.ParticleProfile;
import net.game.spacepirates.particles.systems.AbstractParticleSystem;
import net.game.spacepirates.util.DeferredCalls;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        if(profile == null || !profile.name.equalsIgnoreCase(systemName)) {
            reinit();
        }

        super.update(delta);

        if(system != null && system.shouldFinish()) {
            if(system.isFinished()) {
                system.finish();
            }
            Gdx.app.postRunnable(() -> parentEntity.removeComponent(this));
        }
    }

    public void reinit() {
        Gdx.app.postRunnable(this::reinitImmediate);
    }

    public void reinitImmediate() {
        if(system != null) {
            if(onFinish != null) {
                onFinish.accept(this);
            }
            system.onFinish = null;
            system.finish();
            system = null;
        }

        system = buildSystem();

        if(system != null) {
            system.spawnMatrixSupplier = transform::worldTransform;
        }

        if(onInit != null && system != null) {
            onInit.accept(this, system);
        }

        DeferredCalls.get().post(profile.duration, () -> {
            if(onFinish != null) {
                onFinish.accept(this);
            }
        });
    }

    public AbstractParticleSystem buildSystem() {
        Optional<ParticleProfile> opt = ParticleManager.get().getProfile(systemName);
        if(!opt.isPresent()) {
            return null;
        }

        this.profile = opt.get();

        return ParticleManager.get()
                .buildSystem(this.profile)
                .orElse(null);
    }

    @Override
    public void onRemoveFromEntity(Entity entity) {
        if(system != null) {
            system.beginFinish();
            system = null;
        }
        super.onRemoveFromEntity(entity);
    }
}
