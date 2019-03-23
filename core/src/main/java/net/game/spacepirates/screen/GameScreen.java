package net.game.spacepirates.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisImage;
import net.game.spacepirates.SpacePiratesLauncher;
import net.game.spacepirates.asset.AssetHandler;
import net.game.spacepirates.engine.WorldEngine;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.CollisionComponent;
import net.game.spacepirates.entity.component.ParticleComponent;
import net.game.spacepirates.entity.component.RotationComponent;
import net.game.spacepirates.entity.types.LocalPlayer;
import net.game.spacepirates.particles.system.TexturedTemporalParticleSystem;
import net.game.spacepirates.render.AbstractRenderer;
import net.game.spacepirates.render.BufferedRenderer;
import net.game.spacepirates.system.CannonSystem;
import net.game.spacepirates.system.InputSystem;
import net.game.spacepirates.system.MovementSystem;
import net.game.spacepirates.system.ParticleSystem;
import net.game.spacepirates.world.PhysicsWorld;
import net.game.spacepirates.world.physics.Physics;
import net.game.spacepirates.world.physics.workers.SpawnEntityTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class GameScreen implements Screen {

    AtomicInteger asyncStartupTaskCounter = new AtomicInteger(0);

    private final SpacePiratesLauncher spacePiratesLauncher;
    OrthographicCamera stageCamera;
    ScreenViewport stageViewport;
    Stage stage;

    PhysicsWorld world;
    WorldEngine engine;
    AbstractRenderer renderer;
    VisImage outputImg;

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

        outputImg = new VisImage();

        stage.addActor(outputImg);

        world = new PhysicsWorld();

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(new Vector2(0, 0));
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = new CircleShape();
        fixDef.shape.setRadius(Physics.cm(32));

        fixDef.density = 1f;
        fixDef.friction = 1f;
        fixDef.restitution = 1f;

        ((CircleShape) fixDef.shape).setPosition(new Vector2(fixDef.shape.getRadius(), fixDef.shape.getRadius()));

        SpawnEntityTask task = new SpawnEntityTask(world.getPhysicsWorld(), def, fixDef);
        LocalPlayer player = world.addEntityImmediate(LocalPlayer.class);
        player.velocityComponent.speed = Physics.cm(256);
        player.setEnabled(false);
        asyncStartupTaskCounter.incrementAndGet();
        ForkJoinPool.commonPool().execute(() -> {
            Body body = task.run();
            body.setSleepingAllowed(false);
            body.setAngularDamping(1);
            body.setLinearDamping(1);
            body.setFixedRotation(true);
            ((CollisionComponent) player.rootComponent).body = body;
            player.setEnabled(true);
            asyncStartupTaskCounter.decrementAndGet();
            check();
        });

        Entity floor = world.addEntityImmediate();
        floor.setEnabled(false);
        floor.setRootComponent(new CollisionComponent("Collision"));
        floor.getTransform().setWorldTranslation(new Vector2(Physics.cm(400), Physics.cm(10)));
        def = new BodyDef();
        def.position.set(new Vector2(0, 0));
        fixDef = new FixtureDef();
        fixDef.shape = new PolygonShape();
        ((PolygonShape) fixDef.shape).setAsBox(Physics.cm(400), Physics.cm(5));
        SpawnEntityTask task2 = new SpawnEntityTask(world.getPhysicsWorld(), def, fixDef);
        asyncStartupTaskCounter.incrementAndGet();
        ForkJoinPool.commonPool().execute(() -> {
            ((CollisionComponent) floor.rootComponent).body = task2.run();
            ((CollisionComponent) floor.rootComponent).body.setSleepingAllowed(false);
            floor.setEnabled(true);
            asyncStartupTaskCounter.decrementAndGet();
            check();
        });

        renderer = new BufferedRenderer().setPhysicsWorld(world);
        renderer.init();

        engine = new WorldEngine(world);
        engine.addSystem(new ParticleSystem(world));
        engine.addSystem(new CannonSystem(world));

        Entity texturedParticles = world.addEntityImmediate();

        RotationComponent rotator = texturedParticles.setRootComponent(new RotationComponent("Rotator"));
        rotator.degreesPerSecond = -5;

        ParticleComponent particles = rotator.addComponent(new ParticleComponent("Particles"));
        particles.systemName = "Texture Test";
        particles.onInit = (comp, sys) -> {
            sys.as(TexturedTemporalParticleSystem.class, ttps -> {
                AssetHandler.get().GetAsync("textures/Triskelion_D.png", Texture.class, t -> {
                    ttps.setSpawnTexture(t);
                });
                AssetHandler.get().GetAsync("textures/Triskelion_N.png", Texture.class, t -> {
                    ttps.setColourTexture(t);
                });
//                ttps.setSpawnTextureRef("textures/awesomeface.png");
//                ttps.setColourTextureRef("textures/awesomeface.png");
                ttps.setSize(512, 512);
                ttps.setSpawnMaskChannel(0);
            });
            sys.addUniform("u_initialLife", loc -> {
                Gdx.gl.glUniform1f(loc, 3);
            });
        };
        texturedParticles.getTransform().translate(new Vector2(512, 512));
    }

    void check() {
        if(asyncStartupTaskCounter.get() <= 0) {
            Gdx.app.postRunnable(() -> {
                engine.addSystem(new InputSystem(world));
                engine.addSystem(new MovementSystem(world));
            });
        }
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());

        engine.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderer.render(world.getRenderables());
        outputImg.setDrawable(renderer.getTexture().getTexture());
        outputImg.setVisible(true);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        stageViewport.update(width, height, true);
        outputImg.setBounds(0, 0, width, height);
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
