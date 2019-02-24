package net.game.spacepirates.entity.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.game.spacepirates.data.Transform2D;

import java.util.List;

public abstract class RenderComponent<T extends RenderComponent> extends SceneComponent<T> {

    public RenderComponent(String name) {
        super(name);
    }

    public abstract void collectRenderables(List<RenderProxy> proxies);

    public static class RenderProxy {
        public TextureRegion texture;
        public Color colour;
        public Transform2D transform;
    }

}
