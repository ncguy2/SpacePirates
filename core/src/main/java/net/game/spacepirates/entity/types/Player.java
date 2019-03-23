package net.game.spacepirates.entity.types;

import com.badlogic.gdx.Gdx;
import net.game.spacepirates.asset.SPSprite;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.SpriteComponent;
import net.game.spacepirates.entity.component.VelocityComponent;

public abstract class Player extends Entity implements IEntityDefinition {

    public SpriteComponent spriteComponent;
    public VelocityComponent velocityComponent;

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
    }

    @Override
    public void assemble() {
        addComponent(spriteComponent);
        addComponent(velocityComponent);
    }

    @Override
    public void initTransform() {
    }

}
