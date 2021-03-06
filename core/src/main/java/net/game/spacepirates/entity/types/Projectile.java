package net.game.spacepirates.entity.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import net.game.spacepirates.entity.Entity;
import net.game.spacepirates.entity.component.ParticleComponent;
import net.game.spacepirates.entity.component.SpriteComponent;
import net.game.spacepirates.entity.component.TimedDeathComponent;
import net.game.spacepirates.entity.component.VelocityComponent;

public class Projectile extends Entity implements IEntityDefinition {

    public VelocityComponent velocityComponent;
    public SpriteComponent spriteComponent;
    public ParticleComponent particleComponent;
    public TimedDeathComponent timedDeathComponent;

    public Projectile() {
        super();
        init();
        assemble();
        initTransform();
    }

    @Override
    public void init() {
        velocityComponent = new VelocityComponent("Velocity");
        velocityComponent.speed = 256;
        velocityComponent.direction.set(1, 0);
        velocityComponent.bVolatileDirection = false;

        spriteComponent = new SpriteComponent("Sprite");
        spriteComponent.textureRef = "textures/sprites/base/laserBase.png";
        spriteComponent.colour.set(Color.RED);

        particleComponent = new ParticleComponent("Fire");
        particleComponent.systemName = "Fire";
        particleComponent.onInit = (comp, sys) -> {
            sys.addUniform("u_initialScale", loc -> {
                Gdx.gl.glUniform2f(loc, 1, 1);
            });
            sys.addUniform("u_simSpeed", loc -> {
                Gdx.gl.glUniform1f(loc, 1);
            });
            sys.addUniform("u_devianceRadius", loc -> {
                Gdx.gl.glUniform1f(loc, 24);
            });
        };

        timedDeathComponent = new TimedDeathComponent("Timed death");
        timedDeathComponent.lifeRemaining = 4f;
    }

    @Override
    public void assemble() {
        setRootComponent(timedDeathComponent);

        addComponent(velocityComponent);
        addComponent(spriteComponent);
        addComponent(particleComponent);
    }

    @Override
    public void initTransform() {
        getTransform().rotation = 180;
    }
}
