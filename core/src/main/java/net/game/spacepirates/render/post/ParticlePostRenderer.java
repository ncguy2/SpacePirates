package net.game.spacepirates.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Matrix4;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.geometry.InstancedMesh;
import net.game.spacepirates.particles.ParticleService;
import net.game.spacepirates.render.buffer.FBO;
import net.game.spacepirates.services.Services;
import net.game.spacepirates.util.ArrayUtils;
import net.game.spacepirates.util.ReloadableShaderProgram;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.GL20.GL_INCR;

public class ParticlePostRenderer {

    public static final Color COLOUR = new Color(0, 0, 0, 0);
    protected FBO fbo;

    protected ShaderStorageBufferObject globalIndexBuffer;
    protected ReloadableShaderProgram shader;
    protected Texture texture;
    protected InstancedMesh mesh;

    public ParticlePostRenderer() {
        Map<String, String> params = new HashMap<>();
        params.put("p_BindingPoint", "0");

        globalIndexBuffer = new ShaderStorageBufferObject(Integer.BYTES);
        globalIndexBuffer.init();

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

    public FBO getFbo() {
        return getFbo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public FBO getFbo(int width, int height) {
        if (fbo == null) {
            fbo = new FBO(Pixmap.Format.RGBA8888, width, height, true, true);
        }
        fbo.resize(width, height);
        return fbo;
    }

    public Texture renderToTexture(Matrix4 projection) {
        FBO fbo = getFbo();
        fbo.begin();
        fbo.clear(COLOUR, true, true);

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
        Gdx.gl.glBlendEquation(GL30.GL_FUNC_ADD);

        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilMask(0xFF); // each bit is written to the stencil buffer as is
        Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
        Gdx.gl.glStencilOp(GL_INCR, GL_INCR, GL_INCR);

        int[] allInts = particleService.getAllInts();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(allInts.length * Integer.BYTES);
        for (int allInt : allInts) {
            byte[] bytes = ArrayUtils.intToGLArr(allInt);
            byteBuffer.put(bytes);
        }
        byteBuffer.position(0);
        globalIndexBuffer.setData(byteBuffer);

        globalIndexBuffer.bind(4);

        mesh.instanceCount = allInts.length;
        mesh.render(shader.program(), GL20.GL_TRIANGLES);

        Gdx.gl.glBlendEquation(GL30.GL_FUNC_ADD);
        shader.program().end();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

}
