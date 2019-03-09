package net.game.spacepirates.system;

import net.game.spacepirates.entity.component.CollisionComponent;
import net.game.spacepirates.entity.component.VelocityComponent;
import net.game.spacepirates.world.GameWorld;
import net.game.spacepirates.world.PhysicsWorld;

public class MovementSystem extends AbstractSystem {

    public MovementSystem(GameWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void startup() {

    }

    @Override
    public void update(float delta) {
        //noinspection unchecked
        operatingWorld.getEntitiesWith(VelocityComponent.class, CollisionComponent.class).forEach(e -> {
            VelocityComponent vel = e._get(VelocityComponent.class);
            CollisionComponent col = e._get(CollisionComponent.class);

            vel.speed = 512;

            if(col.body != null) {
                col.body.setLinearVelocity(vel.getVelocity().scl(PhysicsWorld.SCREEN_TO_WORLD));
            }
            vel.reset();
        });
    }

    @Override
    public void shutdown() {

    }
}
