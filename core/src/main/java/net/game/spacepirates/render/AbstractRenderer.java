package net.game.spacepirates.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.game.spacepirates.entity.component.RenderComponent;

import java.util.stream.Stream;

public abstract class AbstractRenderer {

    public abstract void init();
    public abstract void render(Stream<RenderComponent> renderComponents);
    public abstract void resize(int width, int height);
    public abstract void reInit();

    public abstract TextureRegion getTexture();

    public abstract Camera getCamera();
}
