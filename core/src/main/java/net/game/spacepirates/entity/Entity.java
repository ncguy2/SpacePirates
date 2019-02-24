package net.game.spacepirates.entity;

import net.game.spacepirates.data.Transform2D;
import net.game.spacepirates.entity.component.EntityComponent;
import net.game.spacepirates.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Entity {

    public final Transform2D transform;
    public final List<EntityComponent> components;
    public GameWorld world;

    public Entity() {
        transform = new Transform2D();
        components = new ArrayList<>();
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    public <T extends EntityComponent> T addComponent(T component) {
        components.add(component);
        component.onAddToEntity(this);
        return component;
    }

    public <T extends EntityComponent> T removeComponent(T component) {
        components.remove(component);
        component.onRemoveFromEntity(this);
        return component;
    }

    public <T extends EntityComponent> boolean has(Class<T> cls) {
        return components.stream().anyMatch(cls::isInstance);
    }

    public <T extends EntityComponent> Optional<T> get(Class<T> cls) {
        return components.stream().filter(cls::isInstance).map(cls::cast).findFirst();
    }

    public <T extends EntityComponent> T _get(Class<T> cls) {
        return get(cls).orElse(null);
    }

    public void update(float delta) {
        components.forEach(c -> c.update(delta));
    }

    public boolean hasAll(Class<? extends EntityComponent>... components) {
        for (Class<? extends EntityComponent> component : components) {
            if(!has(component)) {
                return false;
            }
        }
        return true;
    }
}
