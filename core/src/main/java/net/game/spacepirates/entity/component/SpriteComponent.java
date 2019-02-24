package net.game.spacepirates.entity.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.game.spacepirates.asset.AssetHandler;

import java.util.List;

public class SpriteComponent extends RenderComponent<SpriteComponent> {

    public String textureRef;
    public final Color colour = new Color(1, 1, 1, 1);

    public SpriteComponent(String name) {
        super(name);
    }

    @Override
    public void collectRenderables(List<RenderProxy> proxies) {
        RenderProxy proxy = new RenderProxy();
        proxy.transform = this.transform.copy();
        AssetHandler.instance().GetAsync(textureRef, Texture.class, t -> proxy.texture = new TextureRegion(t));
        proxy.colour = colour;
        proxies.add(proxy);
    }
}
