package net.game.spacepirates.render.post;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lwjgl.opengl.GL20;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class PostProcessorContext {

    public Texture finalTexture;
    public int depthBufferWidth;
    public int depthBufferHeight;
    public int depthBufferHandle;

    public final SpriteBatch batch;
    public final Camera camera;
    public final float delta;

    public final Map<String, Texture> namedTextures;

    public PostProcessorContext(SpriteBatch batch, Camera camera, float delta) {
        this.batch = batch;
        this.camera = camera;
        this.delta = delta;
        namedTextures = new TreeMap<>();
    }

    public void addTexture(Texture texture, String name) {
        namedTextures.put(name, texture);
    }

    public Optional<Texture> getNamedTexture(String name) {
        return Optional.ofNullable(namedTextures.get(name));
    }

    public Texture[] getTextures() {
        return namedTextures.values().toArray(new Texture[0]);
    }

    public GLTexture getDepthTexture() {
        return new GLTexture(GL20.GL_TEXTURE_2D, depthBufferHandle) {
            @Override
            public int getWidth() {
                return depthBufferWidth;
            }

            @Override
            public int getHeight() {
                return depthBufferHeight;
            }

            @Override
            public int getDepth() {
                return 1;
            }

            @Override
            public boolean isManaged() {
                return false;
            }

            @Override
            protected void reload() {

            }

            @Override
            protected void delete() {
                // Don't delete the shared resource that belongs to something else
            }
        };
    }
}
