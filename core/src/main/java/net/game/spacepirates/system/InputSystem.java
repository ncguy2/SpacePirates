package net.game.spacepirates.system;

import net.game.spacepirates.entity.component.InputComponent;
import net.game.spacepirates.entity.component.VelocityComponent;
import net.game.spacepirates.world.GameWorld;

public class InputSystem extends AbstractSystem {

    public InputSystem(GameWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void startup() {

    }

    @Override
    public void update(float delta) {
        operatingWorld.getEntitiesWith(InputComponent.class, VelocityComponent.class)
                .forEach(e -> {
            InputComponent inputComponent = e._get(InputComponent.class);
            VelocityComponent velocityComponent = e._get(VelocityComponent.class);

            velocityComponent.direction.x += inputComponent.axisHorizontal.resolve();
            velocityComponent.direction.y += inputComponent.axisVertical.resolve();
        });
    }

    @Override
    public void shutdown() {

    }
}
