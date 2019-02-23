package net.game.spacepirates.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.data.Transform2D;
import net.game.spacepirates.entity.component.RenderComponent;

import java.util.List;

public class SimpleRenderer extends AbstractRenderer {

    public OrthographicCamera worldCamera;
    public SpriteBatch batch;

    @Override
    public void init() {
        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
    }

    @Override
    public void renderProxies(List<RenderComponent.RenderProxy> proxyList) {
        batch.begin();

        for (RenderComponent.RenderProxy proxy : proxyList) {
            Sprite s = get(proxy.textureRef);
            s.setColor(proxy.colour);
            Transform2D transform = proxy.transform;
            s.setPosition(transform.translation.x, transform.translation.y);
            s.setRotation(transform.rotation);
            s.setSize(transform.scale.x, transform.scale.y);
            if(s.getTexture() != null) {
                s.draw(batch);
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        worldCamera.setToOrtho(true, width, height);
    }

    public Sprite get(String ref) {
        final Sprite s = new Sprite();
        AssetHandler.instance().GetAsync(ref, Texture.class, s::setTexture);
        return s;
    }

}
