package net.game.spacepirates.entity.types;

import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.CannonComponent;
import net.game.spacepirates.entity.component.MultiSpriteComponent;
import net.game.spacepirates.entity.component.VelocityComponent;

public abstract class Player extends Entity implements IEntityDefinition {

    public MultiSpriteComponent multiSpriteComponent;
    public VelocityComponent velocityComponent;
    public CannonComponent cannonComponent;

    public Player() {
        super();
        init();
        assemble();
        initTransform();
    }

    @Override
    public void init() {
        multiSpriteComponent = new MultiSpriteComponent("Sprites");
        velocityComponent = new VelocityComponent("Velocity");
        cannonComponent = new CannonComponent("Cannon");
    }

    @Override
    public void assemble() {
        addComponent(multiSpriteComponent);
        addComponent(velocityComponent);
        addComponent(cannonComponent);
    }

    @Override
    public void initTransform() {
        transform.scale.set(64, 32);
        cannonComponent.transform.translation.set(64, 28);
    }

}
