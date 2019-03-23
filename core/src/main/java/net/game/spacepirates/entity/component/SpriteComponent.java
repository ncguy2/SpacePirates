package net.game.spacepirates.entity.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.asset.SPAsset;
import net.game.spacepirates.render.RenderContext;

public class SpriteComponent extends RenderComponent<SpriteComponent> {

    public final Color colour = new Color(1, 1, 1, 1);
    public String textureRef;
    private transient Sprite sprite;
    private transient boolean loadingSprite;

    public SpriteComponent(String name) {
        super(name);
    }

    public SpriteComponent(String name, SPAsset asset) {
        super(name);
        sprite = asset.convert(Sprite.class);
    }

    @Override
    public void draw(RenderContext context) {
        if (sprite == null) {
            if (!loadingSprite) {
                loadSprite();
            }
            return;
        }

        Vector2 loc = transform.worldTranslation();
        sprite.setPosition(loc.x, loc.y);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        sprite.draw(context.batch);
    }

    private void loadSprite() {
        loadingSprite = true;
        AssetHandler.get().GetAsync(textureRef, Texture.class, t -> {
            sprite = new Sprite(t);
            loadingSprite = false;
        });
    }

//    @Override
//    public void collectRenderables(List<RenderProxy> proxies) {
//        RenderProxy proxy = new RenderProxy();
//        proxy.transform = this.transform.copy();
//        AssetHandler.get().GetAsync(textureRef, Texture.class, t -> proxy.texture = new TextureRegion(t));
//        proxy.colour = colour;
//        proxies.add(proxy);
//    }
}
