package net.game.spacepirates.system;

import net.game.spacepirates.entity.component.CollisionComponent;
import net.game.spacepirates.entity.component.VelocityComponent;
import net.game.spacepirates.world.GameWorld;

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

            if(col.body != null) {
                col.body.applyLinearImpulse(vel.getVelocity(), col.body.getWorldCenter(), true);
                col.body.setLinearDamping(25);
            }
            vel.reset();
        });
    }

    @Override
    public void shutdown() {

    }
}
