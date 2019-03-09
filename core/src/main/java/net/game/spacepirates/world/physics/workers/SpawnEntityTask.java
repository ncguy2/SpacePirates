package net.game.spacepirates.world.physics.workers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import net.game.spacepirates.world.physics.PhysicsService;

public class SpawnEntityTask extends PhysicsTask<Body> {

    BodyDef bodyDef;
    FixtureDef[] fixtureDefs;

    public SpawnEntityTask(World world, BodyDef bodyDef, FixtureDef... fixtureDefs) {
        super(world);
        this.bodyDef = bodyDef;
        this.fixtureDefs = fixtureDefs;
    }

    public SpawnEntityTask(World world, PhysicsService service, BodyDef bodyDef, FixtureDef... fixtureDefs) {
        super(world, service);
        this.bodyDef = bodyDef;
        this.fixtureDefs = fixtureDefs;
    }

    @Override
    public Body run() {

        CreateBodyTask bodyTask = new CreateBodyTask(world, bodyDef);
        Body body = bodyTask.run();

        for (FixtureDef fixtureDef : fixtureDefs) {
            CreateFixtureTask fixtureTask = new CreateFixtureTask(world, body, fixtureDef);
            fixtureTask.run();
        }

        return body;
    }
}
