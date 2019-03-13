package net.game.spacepirates.entity.component;

import net.game.spacepirates.data.Transform2D;
import net.game.spacepirates.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class SceneComponent<T extends SceneComponent> extends EntityComponent<T> {

    public final Transform2D transform;
    public List<EntityComponent<?>> components;

    public SceneComponent(String name) {
        super(name);
        transform = new Transform2D();
        components = new ArrayList<>();
    }

    public Transform2D getAttachmentTransform() {
        return transform;
    }

    public <U extends EntityComponent<U>> U addComponent(U component) {
        components.add(component);

        if(component instanceof SceneComponent) {
            getAttachmentTransform().adopt(((SceneComponent) component).transform);
        }

        return component;
    }

    public <U extends EntityComponent<U>> U removeComponent(U component) {
        components.remove(component);

        if(component instanceof SceneComponent) {
            getAttachmentTransform().release(((SceneComponent) component).transform);
        }

        return component;
    }

    @Override
    public void onAddToEntity(Entity entity) {
        super.onAddToEntity(entity);
    }

    @Override
    public void onRemoveFromEntity(Entity entity) {
        super.onRemoveFromEntity(entity);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        for (EntityComponent<?> component : components) {
            component.update(delta);
        }
    }
}
