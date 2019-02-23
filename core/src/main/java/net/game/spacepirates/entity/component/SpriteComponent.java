package net.game.spacepirates.entity.component;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class SpriteComponent extends RenderComponent<SpriteComponent> {

    public String textureRef;
    public Color colour;

    public SpriteComponent(String name) {
        super(name);
    }

    @Override
    public void collectRenderables(List<RenderProxy> proxies) {
        RenderProxy proxy = new RenderProxy();
        proxy.transform = this.transform.copy();
        proxy.textureRef = textureRef;
        proxy.colour = colour;
        proxies.add(proxy);
    }
}
