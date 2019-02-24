package net.game.spacepirates.entity.component;

import com.badlogic.gdx.math.Vector2;

public class VelocityComponent extends EntityComponent<VelocityComponent> {

    public final Vector2 direction;
    public float speed = 1;
    public boolean bVolatileDirection = true;

    public VelocityComponent(String name) {
        super(name);
        direction = new Vector2();
    }

    public void setVolatileDirection(boolean bVolatileDirection) {
        this.bVolatileDirection = bVolatileDirection;
    }

    public boolean isVolatileDirection() {
        return bVolatileDirection;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public Vector2 getVelocity() {
        return direction.nor().scl(speed);
    }

    public void reset() {
        if(bVolatileDirection) {
            direction.set(0, 0);
        }
    }


}
