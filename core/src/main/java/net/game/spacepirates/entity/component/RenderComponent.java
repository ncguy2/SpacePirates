package net.game.spacepirates.entity.component;

import com.badlogic.gdx.graphics.Color;
import net.game.spacepirates.data.Transform2D;

import java.util.List;

public abstract class RenderComponent<T extends RenderComponent> extends SceneComponent<T> {

    public RenderComponent(String name) {
        super(name);
    }

    public abstract void collectRenderables(List<RenderProxy> proxies);

    public static class RenderProxy {
        public String textureRef;
        public Color colour;
        public Transform2D transform;
    }

}
