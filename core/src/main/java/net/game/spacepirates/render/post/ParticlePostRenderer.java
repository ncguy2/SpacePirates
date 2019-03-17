package net.game.spacepirates.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.geometry.InstancedMesh;
import net.game.spacepirates.particles.ParticleService;
import net.game.spacepirates.render.buffer.FBO;
import net.game.spacepirates.services.Services;
import net.game.spacepirates.util.ReloadableShaderProgram;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticlePostRenderer {

    public static final Color COLOUR = new Color(0, 0, 0, 0);
    protected FBO fbo;
    protected SpriteBatch spriteBatch;

    protected ReloadableShaderProgram shader;
    protected Texture texture;
    protected InstancedMesh mesh;

    public ParticlePostRenderer() {
        Map<String, String> params = new HashMap<>();
        params.put("p_BindingPoint", "0");

        shader = new ReloadableShaderProgram("Particle Renderer", Gdx.files.internal("particles/render/particle.vert"), Gdx.files.internal("particles/render/particle.frag"), params);
//        AssetHandler.instance().GetAsync("textures/particle/default.png", Texture.class, t -> texture = t);
        texture = AssetHandler.instance().Get("textures/particle/default.png", Texture.class);

        mesh = new InstancedMesh(true, 4, 8 , VertexAttribute.Position(), VertexAttribute.TexCoords(0));

        mesh.setVertices(new float[] {
                -1, -1, 0, 0, 0,
                 1, -1, 0, 1, 0,
                -1,  1, 0, 0, 1,
                 1,  1, 0, 1, 1
        });

        mesh.setIndices(new short[] {
                0, 1, 2,
                1, 3, 2
        });
    }

    public SpriteBatch getBatch() {
        if (spriteBatch == null) {
            spriteBatch = new SpriteBatch();
        }
        return spriteBatch;
    }

    public FBO getFbo() {
        return getFbo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public FBO getFbo(int width, int height) {
        if (fbo == null) {
            fbo = new FBO(Pixmap.Format.RGBA8888, width, height, true);
        }
        fbo.resize(width, height);
        return fbo;
    }

    public Texture renderToTexture(Matrix4 projection) {
        return renderToTexture(null, projection);
    }

    public Texture renderToTexture(Texture base, Matrix4 projection) {
        FBO fbo = getFbo();
        fbo.begin();
        fbo.clear(COLOUR, true, false);

        if(base != null) {
            SpriteBatch batch = getBatch();
            batch.setProjectionMatrix(projection);
            batch.setShader(null);
            batch.draw(base, 0, 0, fbo.width(), fbo.height());
            batch.end();
        }

        render(projection);

        fbo.end();
        return fbo.getColorBufferTexture();
    }

    public void render(Matrix4 projection) {
        if(texture == null) {
            return;
        }

        ParticleService particleService = Services.get(ParticleService.class);

        shader.program().begin();

        texture.bind(8);
        shader.program().setUniformi("u_texture", 8);

        shader.program().setUniformMatrix("u_projViewTrans", projection);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL30.GL_MAX);

        // TODO Render particles
        particleService.getKeys().forEach(key -> {
            List<Integer> indices = particleService.getInts(key);
            mesh.instanceCount = indices.size();
            ShaderStorageBufferObject buffer = key.getIndexBuffer();

            ByteBuffer b = ByteBuffer.allocate(indices.size() * Integer.BYTES);
            indices.stream().sorted().forEach(b::putInt);
            b.position(0);
            buffer.setData(b);

            buffer.bind(4);

            mesh.render(shader.program(), GL20.GL_TRIANGLES);
            buffer.unbind();
        });

        Gdx.gl.glBlendEquation(GL30.GL_FUNC_ADD);
        shader.program().end();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

}
