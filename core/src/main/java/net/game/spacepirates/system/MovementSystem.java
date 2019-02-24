package net.game.spacepirates.system;

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
        operatingWorld.getEntitiesWith(VelocityComponent.class).forEach(e -> {
            VelocityComponent vel = e._get(VelocityComponent.class);
            e.transform.translation.add(vel.getVelocity().scl(delta));
            vel.reset();
        });
    }

    @Override
    public void shutdown() {

    }
}
