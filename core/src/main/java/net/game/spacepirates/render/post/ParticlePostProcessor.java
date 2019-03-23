package net.game.spacepirates.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import net.game.spacepirates.asset.Sprites;
import net.game.spacepirates.render.buffer.FBO;
import net.game.spacepirates.util.curve.GLColourCurve;

import static com.badlogic.gdx.graphics.GL20.*;

public class ParticlePostProcessor extends AbstractPostProcessor<ParticlePostProcessor> {

    private ParticlePostRenderer renderer;
    private FBO fbo;
    private SpriteBatch batch;
    private GLColourCurve colourCurve;

    public static final String PARTICLE_TEXTURE_NAME = "texture.particles";
    public static final String PARTICLE_OVERDRAW_NAME = "texture.particles.overdraw";

    @Override
    public ParticlePostProcessor init() {
        renderer = new ParticlePostRenderer();
        fbo = new FBO(Pixmap.Format.RGBA8888, renderer.getFbo().width(), renderer.getFbo().height(), true, true);
        batch = new SpriteBatch();

        colourCurve = new GLColourCurve();
        colourCurve.Add(Color.BLACK, 0);
        colourCurve.Add(Color.CYAN, 1);
        colourCurve.Add(Color.GREEN, 2);
        colourCurve.Add(Color.YELLOW, 3);
        colourCurve.Add(Color.RED, 4);

        return this;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        fbo.resize(width, height);
    }

    @Override
    public void _render(PostProcessorContext context) {
        Texture texture = renderer.renderToTexture(context.camera.combined);
        context.addTexture(texture, PARTICLE_TEXTURE_NAME);

        fbo.begin();
        fbo.clear(new Color(0, 0, 0, 0.8f), true, true);
        fbo.end();

        renderer.getFbo().blit(fbo, GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT, GL_NEAREST);

        fbo.begin();
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, fbo.width(), fbo.height()));

        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

        Texture pixel = Sprites.pixelTexture();
        batch.begin();

        for (int i = 1; i < 255; i++) {
            Color col = colourCurve.Sample(i);
            batch.setColor(col);
            Gdx.gl.glStencilFunc(GL_EQUAL, i, 0xFF);
            batch.draw(pixel, 0, 0, fbo.width(), fbo.height());
            batch.flush();
        }

        batch.end();
        fbo.end();

        Gdx.gl.glColorMask(true, true, true, true);

        context.addTexture(fbo.getColorBufferTexture(), PARTICLE_OVERDRAW_NAME);

        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);

    }

    @Override
    public void shutdown() {

    }
}
