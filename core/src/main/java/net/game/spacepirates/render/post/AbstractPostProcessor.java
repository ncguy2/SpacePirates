package net.game.spacepirates.render.post;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import net.game.spacepirates.render.buffer.FBO;
import net.game.spacepirates.util.ReloadableShaderProgram;

public abstract class AbstractPostProcessor<T extends AbstractPostProcessor> {

    protected FBO framebuffer;
    protected ReloadableShaderProgram shader;
    protected boolean enabled = true;

    public void resize(int width, int height) {
        if(framebuffer != null) {
            framebuffer.resize(width, height);
        }
    }

    public abstract T init();
    public Texture[] render(Batch batch, Camera camera, Texture[] input, float delta) {
        if(enabled) {
            return _render(batch, camera, input, delta);
        }
        return input;
    }

    public abstract Texture[] _render(Batch batch, Camera camera, Texture[] input, float delta);
    public abstract void shutdown();
}
