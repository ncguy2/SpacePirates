package net.game.spacepirates.entity.component;

public abstract class EntityComponent<T extends EntityComponent> {

    public final String name;

    public EntityComponent(String name) {
        this.name = name;
    }

    public void update(float delta) {

    }

}
