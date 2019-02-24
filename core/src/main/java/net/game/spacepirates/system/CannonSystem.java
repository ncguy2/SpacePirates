package net.game.spacepirates.system;

import net.game.spacepirates.entity.component.CannonComponent;
import net.game.spacepirates.entity.component.InputComponent;
import net.game.spacepirates.world.GameWorld;

public class CannonSystem extends AbstractSystem {

    public CannonSystem(GameWorld operatingWorld) {
        super(operatingWorld);
    }

    @Override
    public void startup() {

    }

    @Override
    public void update(float delta) {
        operatingWorld.getEntitiesWith(InputComponent.class, CannonComponent.class).forEach(e -> {
            InputComponent inputComponent = e._get(InputComponent.class);
            CannonComponent cannonComponent = e._get(CannonComponent.class);

            boolean test = inputComponent.keyFire.test();
            if(test && !cannonComponent.hasFired) {
                cannonComponent.fire();
            }else if(!test && cannonComponent.hasFired) {
                cannonComponent.hasFired = false;
            }
        });
    }

    @Override
    public void shutdown() {

    }
}
