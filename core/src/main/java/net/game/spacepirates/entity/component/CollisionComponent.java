package net.game.spacepirates.entity.component;

import com.badlogic.gdx.physics.box2d.Body;

public class CollisionComponent extends SceneComponent<CollisionComponent> {

    public CollisionComponent(String name) {
        super(name);
    }

    public Body body;

}
