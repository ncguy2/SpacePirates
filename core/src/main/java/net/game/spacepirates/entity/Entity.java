package net.game.spacepirates.entity;

import net.game.spacepirates.data.Transform2D;
import net.game.spacepirates.entity.component.EntityComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Entity {

    public final Transform2D transform;
    public final List<EntityComponent> components;

    public Entity() {
        transform = new Transform2D();
        components = new ArrayList<>();
    }

    public <T extends EntityComponent> T addComponent(T component) {
        components.add(component);
        return component;
    }

    public <T extends EntityComponent> boolean has(Class<T> cls) {
        return components.stream().anyMatch(cls::isInstance);
    }

    public <T extends EntityComponent> Optional<T> get(Class<T> cls) {
        return components.stream().filter(cls::isInstance).map(cls::cast).findFirst();
    }

    public void update(float delta) {
        components.forEach(c -> c.update(delta));
    }
}
