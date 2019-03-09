package net.game.spacepirates.entity.types;

import com.badlogic.gdx.Gdx;
import net.game.spacepirates.asset.SPSprite;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.CannonComponent;
import net.game.spacepirates.entity.component.SpriteComponent;
import net.game.spacepirates.entity.component.VelocityComponent;

public abstract class Player extends Entity implements IEntityDefinition {

    public SpriteComponent spriteComponent;
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
        spriteComponent = new SpriteComponent("Sprite", SPSprite.of(Gdx.files.internal("data/awesomeface.json")));
        velocityComponent = new VelocityComponent("Velocity");
        cannonComponent = new CannonComponent("Cannon");
    }

    @Override
    public void assemble() {
        addComponent(spriteComponent);
        addComponent(velocityComponent);
        addComponent(cannonComponent);
    }

    @Override
    public void initTransform() {
    }

}
