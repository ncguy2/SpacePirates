package net.game.spacepirates.entity.component;

import net.game.spacepirates.entity.Entity;

public abstract class EntityComponent<T extends EntityComponent> {

    public final String name;
    public Entity parentEntity;

    public EntityComponent(String name) {
        this.name = name;
    }

    public void update(float delta) {}

    public void onAddToEntity(Entity entity) {
        this.parentEntity = entity;
    }
    public void onRemoveFromEntity(Entity entity) {
        this.parentEntity = null;
    }

}
