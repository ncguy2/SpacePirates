package net.game.spacepirates.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.game.spacepirates.entity.component.RenderComponent;
import net.game.spacepirates.input.PostProcessingCamera;
import net.game.spacepirates.render.post.EmissivePostProcessor;
import net.game.spacepirates.render.post.ParticlePostProcessor;
import net.game.spacepirates.world.PhysicsWorld;

import java.util.stream.Stream;

public class BufferedRenderer extends AbstractRenderer {

    public PhysicsWorld physicsWorld;
    public OrthographicCamera worldCamera;
    public SpriteBatch batch;
    public ShaderProgram shader;
    public PostProcessingCamera<OrthographicCamera> ppCamera;
    protected TextureRegion region;

    public BufferedRenderer setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
        return this;
    }

    @Override
    public void init() {
        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        ppCamera = new PostProcessingCamera<>(worldCamera, new ParticlePostProcessor(), new EmissivePostProcessor("texture.emissive", ParticlePostProcessor.PARTICLE_TEXTURE_NAME));
        region = new TextureRegion();
        loadShader();
    }

    public void loadShader() {
        FileHandle vert = Gdx.files.internal("shaders/buffered/buffered.vert");
        if(!vert.exists()) {
            System.out.println("Cannot find " + vert.toString());
            return;
        }

        FileHandle frag = Gdx.files.internal("shaders/buffered/buffered.frag");
        if(!frag.exists()) {
            System.out.println("Cannot find " + frag.toString());
            return;
        }

        ShaderProgram s = new ShaderProgram(vert, frag);
        if(!s.isCompiled()) {
            System.out.println(s.getLog());
            s.dispose();
            return;
        }
        if(shader != null) {
            shader.dispose();
        }

        shader = s;

        if(batch != null) {
            batch.setShader(shader);
        }
    }

    @Override
    public void render(Stream<RenderComponent> renderComponents) {

        RenderContext context = new RenderContext();
        context.batch = batch;
        context.camera = worldCamera;

        ppCamera.begin();
        ppCamera.clear(Color.BLACK, true, false);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.setShader(shader);
        batch.begin();
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        renderComponents.forEach(c -> c.draw(context));

        batch.end();

        ShapeRenderHost.get().draw(worldCamera.combined);

        if(physicsWorld != null) {
            physicsWorld.debugRender(worldCamera.combined.cpy());
        }

        ppCamera.end();

        region.setTexture(ppCamera.processAndFlatten(batch, Gdx.graphics.getDeltaTime()));
        if((ppCamera.processors.size() & 1) == 1 != region.isFlipY()) {
            region.flip(false, true);
        }
    }

    @Override
    public void resize(int width, int height) {
        worldCamera.setToOrtho(false, width, height);
        ppCamera.resize(width, height);
    }

    @Override
    public void reInit() {
        loadShader();
    }

    @Override
    public TextureRegion getTexture() {
        return region;
    }

    @Override
    public Camera getCamera() {
        return worldCamera;
    }

    public Sprite get(TextureRegion reg) {
        if(reg == null || reg.getTexture() == null) {
            return null;
        }
        Sprite s = new Sprite();
        s.setRegion(reg);
        return s;
    }

}
