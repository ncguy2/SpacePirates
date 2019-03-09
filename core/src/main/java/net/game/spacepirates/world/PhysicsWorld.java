package net.game.spacepirates.world;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import net.game.spacepirates.entity.component.CollisionComponent;
import net.game.spacepirates.world.physics.PhysicsService;
import net.game.spacepirates.world.physics.impl.PhysicsServiceImpl;

public class PhysicsWorld extends GameWorld {

    private World physicsWorld;
    private Box2DDebugRenderer debugRenderer;
    private float accumulator;
    private PhysicsService physicsService;

    public static final float SCREEN_TO_WORLD = 0.01f;
    public static final float WORLD_TO_SCREEN = 1f / SCREEN_TO_WORLD;

    public PhysicsWorld() {
        physicsWorld = new World(Vector2.Zero, true);
        debugRenderer = new Box2DDebugRenderer();
        // Initialise and register physics service
        physicsService = new PhysicsServiceImpl(physicsWorld);
    }

    public void debugRender(Matrix4 projection) {
        debugRenderer.render(physicsWorld, projection.scl(WORLD_TO_SCREEN));
    }

    @Override
    public synchronized void update(float delta) {
        super.update(delta);
        physicsService.produce();
        physicsService.execute();
        bubbleToPhysics();
        doPhysicsStep(delta);
        cascadeToComponent();
        physicsService.remove();
    }

    private void bubbleToPhysics() {
        getEntitiesWith(CollisionComponent.class).forEach(entity -> {
            CollisionComponent collision = entity._get(CollisionComponent.class);

            if(collision.body != null) {
                collision.body.setTransform(new Vector2(collision.transform.worldTranslation()).scl(SCREEN_TO_WORLD), collision.transform.worldRotation());
            }
        });
    }

    private void cascadeToComponent() {
        getEntitiesWith(CollisionComponent.class).forEach(entity -> {
            CollisionComponent collision = entity._get(CollisionComponent.class);

            if(collision.body != null) {
                Transform bodyTransform = collision.body.getTransform();
                collision.transform.setWorldTranslation(new Vector2(bodyTransform.getPosition()).scl(WORLD_TO_SCREEN));
                collision.transform.setWorldRotationRad(bodyTransform.getRotation());
            }
        });
    }

    private void doPhysicsStep(float delta) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;
        float timeStep = 1f / 45f;
        while (accumulator >= timeStep) {
            try {
                physicsWorld.step(timeStep, 6, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            accumulator -= timeStep;
        }
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }
}
