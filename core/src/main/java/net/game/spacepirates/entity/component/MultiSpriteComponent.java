package net.game.spacepirates.entity.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.game.spacepirates.asset.AssetHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiSpriteComponent extends RenderComponent<MultiSpriteComponent> {

    public Set<String> textureRefs;
    public final Color colour = new Color(1, 1, 1, 1);

    public MultiSpriteComponent(String name) {
        super(name);
        textureRefs = new HashSet<>();
    }

    @Override
    public void collectRenderables(List<RenderProxy> proxies) {
        textureRefs.stream().map(this::toProxy).forEach(proxies::add);
    }

    private RenderProxy toProxy(String texRef) {
        RenderProxy proxy = new RenderProxy();
        proxy.transform = this.transform.copy();
        AssetHandler.instance().GetAsync(texRef, Texture.class, t -> proxy.texture = new TextureRegion(t));
        proxy.colour = colour;
        return proxy;
    }

    public void addRef(String ref) {
        textureRefs.add(ref);
    }
}
