package net.game.spacepirates.data;

import com.badlogic.gdx.math.Vector2;

public class Transform2D {

    public final Vector2 translation = new Vector2();
    public float rotation;
    public final Vector2 scale = new Vector2(1, 1);

    public Transform2D() {
    }

    public Transform2D(Transform2D transform) {
        this.translation.set(transform.translation);
        this.rotation = transform.rotation;
        this.scale.set(transform.scale);
    }

    public Transform2D copy() {
        return new Transform2D(this);
    }

}
