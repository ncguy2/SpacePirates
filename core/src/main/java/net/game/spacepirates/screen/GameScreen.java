package net.game.spacepirates.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import net.game.spacepirates.SpacePiratesLauncher;
import net.game.spacepirates.engine.WorldEngine;
import net.game.spacepirates.entity.types.LocalPlayer;
import net.game.spacepirates.render.AbstractRenderer;
import net.game.spacepirates.render.SimpleRenderer;
import net.game.spacepirates.system.CannonSystem;
import net.game.spacepirates.system.InputSystem;
import net.game.spacepirates.system.MovementSystem;
import net.game.spacepirates.world.GameWorld;

public class GameScreen implements Screen {

    private final SpacePiratesLauncher spacePiratesLauncher;
    OrthographicCamera stageCamera;
    ScreenViewport stageViewport;
    Stage stage;

    GameWorld world;
    WorldEngine engine;
    AbstractRenderer renderer;

    public GameScreen(SpacePiratesLauncher spacePiratesLauncher) {
        this.spacePiratesLauncher = spacePiratesLauncher;
    }

    public AbstractRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void show() {
        stageCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageViewport = new ScreenViewport(stageCamera);
        stage = new Stage(stageViewport);

        world = new GameWorld();

//        Entity entity = world.addEntityImmediate();
//        SpriteComponent sprite = new SpriteComponent("Sprite");
//        sprite.textureRef = "textures/awesomeface.png";
//        sprite.transform.scale.set(128, 128);
//        entity.addComponent(sprite);
//        entity.addComponent(new VelocityComponent("Velocity")).speed = 100;
//        entity.addComponent(new InputComponent("Input"));

        LocalPlayer player = world.addEntityImmediate(LocalPlayer.class);
        player.multiSpriteComponent.addRef("textures/sprites/craft/parts/engine/engine3.png");
        player.multiSpriteComponent.addRef("textures/sprites/craft/parts/gun/gun2.png");
        player.multiSpriteComponent.addRef("textures/sprites/craft/parts/hull/hull2.png");
        player.multiSpriteComponent.addRef("textures/sprites/craft/parts/mast/mast2.png");
        player.multiSpriteComponent.addRef("textures/sprites/craft/parts/sail/sail4.png");
        player.velocityComponent.speed = 100;

        renderer = new SimpleRenderer();
        renderer.init();

        engine = new WorldEngine(world);
        engine.addSystem(new InputSystem(world));
        engine.addSystem(new MovementSystem(world));
        engine.addSystem(new CannonSystem(world));
    }

    @Override
    public void render(float delta) {
        engine.update(delta);

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
