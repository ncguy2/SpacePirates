package net.game.spacepirates.entity.component;

import net.game.spacepirates.render.RenderContext;

public abstract class RenderComponent<T extends RenderComponent> extends SceneComponent<T> {

    public RenderComponent(String name) {
        super(name);
    }

    public abstract void draw(RenderContext context);

}
