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

            float up = inputComponent.keyUp.test() ? 1f : 0f;
            float down = inputComponent.keyDown.test() ? 1f : 0f;
            float left = inputComponent.keyLeft.test() ? 1f : 0f;
            float right = inputComponent.keyRight.test() ? 1f : 0f;

            velocityComponent.direction.x -= left;
            velocityComponent.direction.x += right;
            velocityComponent.direction.y += up;
            velocityComponent.direction.y -= down;
        });
    }

    @Override
    public void shutdown() {

    }
}
