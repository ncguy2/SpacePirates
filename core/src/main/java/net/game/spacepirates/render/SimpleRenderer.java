package net.game.spacepirates.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;
import net.game.spacepirates.entity.component.RenderComponent;

import java.util.stream.Stream;

@Deprecated
public class SimpleRenderer extends AbstractRenderer {

    public OrthographicCamera worldCamera;
    public SpriteBatch batch;
    public ShaderProgram shader;

    Pool<Sprite> spritePool = new Pool<Sprite>() {
        @Override
        protected Sprite newObject() {
            return new Sprite();
        }
    };

    @Override
    public void init() {
        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        loadShader();
    }

    public void loadShader() {
        FileHandle vert = Gdx.files.internal("shaders/simple/simple.vert");
        if(!vert.exists()) {
            System.out.println("Cannot find " + vert.toString());
            return;
        }
        
        FileHandle frag = Gdx.files.internal("shaders/simple/simple.frag");
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

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // TODO add sorting
        renderComponents.forEach(c -> c.draw(context));

        batch.end();

        ShapeRenderHost.get().draw(worldCamera.combined);
    }

    @Override
    public void resize(int width, int height) {
        worldCamera.setToOrtho(false, width, height);
    }

    @Override
    public void reInit() {
        loadShader();
    }

    @Override
    public TextureRegion getTexture() {
        return null;
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
