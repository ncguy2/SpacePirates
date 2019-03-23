package net.game.spacepirates.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import net.game.spacepirates.render.buffer.FBO;
import net.game.spacepirates.util.ReloadableShaderProgram;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.badlogic.gdx.graphics.GL20.GL_FLOAT;
import static com.badlogic.gdx.graphics.GL20.GL_RGBA;
import static com.badlogic.gdx.graphics.GL30.GL_RGBA32F;

public class EmissivePostProcessor extends AbstractPostProcessor<EmissivePostProcessor> {

    public static final String EMISSIVE_TEXTURE_NAME = "texture.emissive.post.additive";
    private final List<String> emissiveTextures;
    private float intensityThreshold = 0.7f;
    private float intensityScale = 5f;
    private SpriteBatch batch;

    public EmissivePostProcessor(String... textureRefs) {
        emissiveTextures = Arrays.asList(textureRefs);
    }

    @Override
    public EmissivePostProcessor init() {
        FBO.Builder builder = new FBO.Builder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        builder.addColorTextureAttachment(GL_RGBA32F, GL_RGBA, GL_FLOAT); // Emissive
        this.framebuffer = builder.buildFbo();

        batch = new SpriteBatch();
        shader = new ReloadableShaderProgram("Emissive shader", Gdx.files.internal("shaders/emissive/emissive.vert"), Gdx.files.internal("shaders/emissive/emissive.frag")) {
            @Override
            public void reloadImmediate() {
                super.reloadImmediate();
                batch.setShader(this.program());
            }
        };

        return this;
    }

    @Override
    public void _render(PostProcessorContext context) {

        framebuffer.begin();
        framebuffer.clear(new Color(0, 0, 0,0), true, true);

        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, framebuffer.height(), framebuffer.width(), -framebuffer.height()));
        batch.begin();

        intensityThreshold = 0.2f;
        intensityScale = 5f;
        shader.program().setUniformf("u_threshold", intensityThreshold);
        shader.program().setUniformf("u_intensityScale", intensityScale);

        for (String ref : emissiveTextures) {
            Optional<Texture> textureOpt = context.getNamedTexture(ref);
            if(!textureOpt.isPresent()) {
                continue;
            }
            batch.draw(textureOpt.get(), 0, 0, framebuffer.width(), framebuffer.height());
        }

        batch.end();

        framebuffer.end();

        context.addTexture(framebuffer.getColorBufferTexture(), EMISSIVE_TEXTURE_NAME);
    }

    @Override
    public void shutdown() {

    }
}
