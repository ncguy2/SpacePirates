package net.game.spacepirates.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.SpriteComponent;
import net.game.spacepirates.render.AbstractRenderer;
import net.game.spacepirates.render.SimpleRenderer;
import net.game.spacepirates.world.GameWorld;

public class GameScreen implements Screen {

    OrthographicCamera stageCamera;
    ScreenViewport stageViewport;
    Stage stage;

    GameWorld world;
    AbstractRenderer renderer;

    @Override
    public void show() {
        stageCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageViewport = new ScreenViewport(stageCamera);
        stage = new Stage(stageViewport);

        world = new GameWorld();

        Entity entity = world.addEntity();
        entity.addComponent(new SpriteComponent("Sprite")).textureRef = "textures/awesomeface.png";

        renderer = new SimpleRenderer();
        renderer.init();
    }

    @Override
    public void render(float delta) {
        world.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderer.renderProxies(world.getRenderProxies());

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        stageViewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
