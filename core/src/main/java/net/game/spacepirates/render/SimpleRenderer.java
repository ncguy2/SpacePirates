package net.game.spacepirates.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;
import net.game.spacepirates.data.Transform2D;
import net.game.spacepirates.entity.component.RenderComponent;

import java.util.ArrayList;
import java.util.List;

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
    public void renderProxies(List<RenderComponent.RenderProxy> proxyList) {
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        List<Sprite> usedSprites = new ArrayList<>();

        for (RenderComponent.RenderProxy proxy : proxyList) {
            Sprite s = get(proxy.texture);
            if(s == null) {
                continue;
            }
            usedSprites.add(s);
            s.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            s.setColor(proxy.colour);
            Transform2D transform = proxy.transform;
            s.setPosition(transform.translation.x, transform.translation.y);
            s.setRotation(transform.rotation);
            s.setSize(transform.scale.x, transform.scale.y);
//            s.setU2(1);
//            s.setV2(1);
            s.draw(batch);
        }

        batch.end();

        usedSprites.forEach(spritePool::free);
        usedSprites.clear();

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

    public Sprite get(TextureRegion reg) {
        if(reg == null || reg.getTexture() == null) {
            return null;
        }
        Sprite s = new Sprite();
        s.setRegion(reg);
        return s;
    }

}
