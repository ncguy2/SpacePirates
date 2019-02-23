package net.game.spacepirates.entity.component;

import net.game.spacepirates.data.Transform2D;

public abstract class SceneComponent<T extends SceneComponent> extends EntityComponent<T> {

    public final Transform2D transform;

    public SceneComponent(String name) {
        super(name);
        transform = new Transform2D();
    }
}
