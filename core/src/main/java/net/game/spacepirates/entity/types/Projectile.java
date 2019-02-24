package net.game.spacepirates.entity.types;

import com.badlogic.gdx.graphics.Color;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.SpriteComponent;
import net.game.spacepirates.entity.component.VelocityComponent;

public class Projectile extends Entity implements IEntityDefinition {

    public VelocityComponent velocityComponent;
    public SpriteComponent spriteComponent;

    public Projectile() {
        super();
        init();
        assemble();
        initTransform();
    }

    @Override
    public void init() {
        velocityComponent = new VelocityComponent("Velocity");
        velocityComponent.speed = 1024;
        velocityComponent.direction.set(1, 0);
        velocityComponent.bVolatileDirection = false;

        spriteComponent = new SpriteComponent("Sprite");
        spriteComponent.textureRef = "textures/sprites/base/laserBase.png";
        spriteComponent.colour.set(Color.RED);
    }

    @Override
    public void assemble() {
        addComponent(velocityComponent);
        addComponent(spriteComponent);
    }

    @Override
    public void initTransform() {
        transform.scale.set(16, 8);
        transform.rotation = 180;
    }
}
