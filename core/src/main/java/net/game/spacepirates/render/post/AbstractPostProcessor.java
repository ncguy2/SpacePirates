package net.game.spacepirates.render.post;

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
    public void render(PostProcessorContext context) {
        if(enabled) {
            _render(context);
        }
    }

    public abstract void _render(PostProcessorContext context);
    public abstract void shutdown();
}
