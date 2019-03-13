package net.game.spacepirates.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.geometry.InstancedMesh;
import net.game.spacepirates.render.buffer.FBO;
import net.game.spacepirates.util.ReloadableShaderProgram;

import java.util.HashMap;
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
        AssetHandler.instance().GetAsync("textures/particle/default.png", Texture.class, t -> texture = t);

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
        shader.program().begin();

        shader.program().setUniformMatrix("u_projViewTrans", projection);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL30.GL_MAX);

        // TODO Render particles

        Gdx.gl.glBlendEquation(GL30.GL_FUNC_ADD);
        shader.program().end();
    }

}
