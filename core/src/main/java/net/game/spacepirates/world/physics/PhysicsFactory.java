package net.game.spacepirates.world.physics;

import com.badlogic.gdx.physics.box2d.*;

public abstract class PhysicsFactory {

    protected World world;

    public PhysicsFactory(World world) {
        this.world = world;
    }

    public abstract Body createBody(BodyDef def);
    public abstract Fixture createFixture(Body body, FixtureDef def);
    public abstract Fixture[] createFixtures(Body body, FixtureDef... defs);
    public abstract Joint createJoint(JointDef def);

}
