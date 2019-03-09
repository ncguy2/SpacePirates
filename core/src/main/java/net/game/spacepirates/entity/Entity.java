package net.game.spacepirates.entity;

import net.game.spacepirates.data.Transform2D;
import net.game.spacepirates.entity.component.EntityComponent;
import net.game.spacepirates.entity.component.SceneComponent;
import net.game.spacepirates.util.EntityUtils;
import net.game.spacepirates.world.GameWorld;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Entity {

    public GameWorld world;
    public SceneComponent<?> rootComponent;

    private boolean enabled = true;

    public Entity() {
        rootComponent = defaultRootComponent();
    }

    public Transform2D getTransform() {
        return rootComponent.transform;
    }

    public synchronized boolean isEnabled() {
        return enabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SceneComponent<?> defaultRootComponent() {
        return new SceneComponent<>("Root");
    }

    public void setRootComponent(SceneComponent<?> rootComponent) {
        if (this.rootComponent != null) {
            this.rootComponent.onRemoveFromEntity(this);
        }

        this.rootComponent = rootComponent;

        if (this.rootComponent != null) {
            this.rootComponent.onAddToEntity(this);
        }
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    public <T extends EntityComponent> T addComponent(T component) {
        rootComponent.addComponent(component);
        component.onAddToEntity(this);
        return component;
    }

    public <T extends EntityComponent> T removeComponent(T component) {
        rootComponent.removeComponent(component);
        component.onRemoveFromEntity(this);
        return component;
    }

    public <T extends EntityComponent> boolean has(Class<T> cls) {
        return EntityUtils.flattenComponents(this).anyMatch(cls::isInstance);
    }

    public <T extends EntityComponent> Optional<T> get(Class<T> cls) {
        return EntityUtils.flattenComponents(this).filter(cls::isInstance).map(cls::cast).findFirst();
    }

    public <T extends EntityComponent> List<T> getAll(Class<T> cls) {
        return EntityUtils.flattenComponents(this).filter(cls::isInstance).map(cls::cast).collect(Collectors.toList());
    }

    public <T extends EntityComponent> T _get(Class<T> cls) {
        return get(cls).orElse(null);
    }

    public void update(float delta) {
        rootComponent.update(delta);
    }

    public boolean hasAll(Class<? extends EntityComponent>... components) {
        for (Class<? extends EntityComponent> component : components) {
            if (!has(component)) {
                return false;
            }
        }
        return true;
    }
}
