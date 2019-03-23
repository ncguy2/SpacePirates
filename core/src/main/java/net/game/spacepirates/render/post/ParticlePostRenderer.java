package net.game.spacepirates.render.post;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
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

    public static final boolean useInstancedRendering = true;

    public float renderScale = 1;

    public ParticlePostRenderer() {
        Map<String, String> params = new HashMap<>();
        params.put("p_BindingPoint", "0");

        globalIndexBuffer = new ShaderStorageBufferObject(Integer.BYTES);
        globalIndexBuffer.init();

        String path;
        if(useInstancedRendering) {
            path = "particles/render/particleInstanced.vert";
        }else{
            path = "particles/render/particleBatched.vert";
        }

        shader = new ReloadableShaderProgram("Particle Renderer", Gdx.files.internal(path), Gdx.files.internal("particles/render/particle.frag"), params);
//        AssetHandler.get().GetAsync("textures/particle/default.png", Texture.class, t -> texture = t);
        texture = AssetHandler.get().Get("textures/particle/default.png", Texture.class);

        int circleMeshPoints = 16;

        VertexAttribute positionAttr = VertexAttribute.Position();
        int vertexStride = positionAttr.numComponents;

        float[] verts = new float[(circleMeshPoints + 1) * vertexStride];
        short[] indxs = new short[circleMeshPoints + 2];

        verts[0] = 0;
        verts[1] = 0;
        verts[2] = 0;

        int ptr = 3;

        float stepAngle = 360 / (float) circleMeshPoints;

        indxs[0] = 0;
        int idxPtr = 1;

        for (int i = 0; i < circleMeshPoints; i++) {

            float angle = stepAngle * i;
            Vector2 dir = Vector2.X.cpy().setAngle(angle).nor();

            verts[ptr++] = dir.x; // X
            verts[ptr++] = dir.y; // Y
            verts[ptr++] = 0; // Z

            indxs[idxPtr++] = (short) (i + 1);
        }
        //noinspection UnusedAssignment
        indxs[idxPtr++] = indxs[1];

        mesh = new InstancedMesh(true, verts.length, indxs.length, positionAttr);

        mesh.setVertices(verts);
        mesh.setIndices(indxs);
    }

    public FBO getFbo() {
        return getFbo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public FBO getFbo(int width, int height) {

        renderScale = 0.6f;
        renderScale = Math.max(0.1f, Math.min(renderScale, 2.0f));

        if (fbo == null) {
            fbo = new FBO(Pixmap.Format.RGBA8888, width, height, true, true);
        }
        fbo.resize(Math.round(width * renderScale), Math.round(height * renderScale));
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
        shader.program().setUniformi("u_particleCount", allInts.length);
        mesh.render(shader.program(), GL20.GL_TRIANGLE_FAN);

        Gdx.gl.glBlendEquation(GL30.GL_FUNC_ADD);
        shader.program().end();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }

}
