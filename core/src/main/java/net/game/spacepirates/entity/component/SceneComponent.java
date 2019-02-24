package net.game.spacepirates.entity.component;

import net.game.spacepirates.data.Transform2D;
import net.game.spacepirates.entity.Entity;

public abstract class SceneComponent<T extends SceneComponent> extends EntityComponent<T> {

    public final Transform2D transform;

    public SceneComponent(String name) {
        super(name);
        transform = new Transform2D();
    }

    @Override
    public void onAddToEntity(Entity entity) {
        super.onAddToEntity(entity);
        transform.setParent(entity.transform);
    }

    @Override
    public void onRemoveFromEntity(Entity entity) {
        super.onRemoveFromEntity(entity);
        transform.clearParent();
    }
}
