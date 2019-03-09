package net.game.spacepirates.world.physics.impl;

import com.badlogic.gdx.physics.box2d.*;
import net.game.spacepirates.world.physics.PhysicsFactory;

import java.util.Arrays;

public class PhysicsFactoryImpl extends PhysicsFactory {

    public PhysicsFactoryImpl(World world) {
        super(world);
    }

    @Override
    public Body createBody(BodyDef def) {
        return world.createBody(def);
    }

    @Override
    public Fixture createFixture(Body body, FixtureDef def) {
        return body.createFixture(def);
    }

    @Override
    public Fixture[] createFixtures(Body body, FixtureDef... defs) {
        return Arrays.stream(defs)
                .map(body::createFixture)
                .toArray(Fixture[]::new);
    }

    @Override
    public Joint createJoint(JointDef def) {
        return world.createJoint(def);
    }
}
