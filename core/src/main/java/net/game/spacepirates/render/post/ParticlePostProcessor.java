package net.game.spacepirates.render.post;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class ParticlePostProcessor extends AbstractPostProcessor<ParticlePostProcessor> {

    private ParticlePostRenderer renderer;

    @Override
    public ParticlePostProcessor init() {
        renderer = new ParticlePostRenderer();
        return this;
    }

    @Override
    public Texture[] _render(Batch batch, Camera camera, Texture[] input, float delta) {
        Texture[] output = new Texture[input.length + 1];
        System.arraycopy(input, 0, output, 0, input.length);
        output[input.length] = renderer.renderToTexture(camera.combined);
        return output;
    }

    @Override
    public void shutdown() {

    }
}
