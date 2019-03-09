package net.game.spacepirates.world.physics.workers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import net.game.spacepirates.world.physics.PhysicsService;

public class CreateFixtureTask extends PhysicsTask<Fixture> {

    protected final Body body;
    protected final FixtureDef def;

    public CreateFixtureTask(World world, Body body, FixtureDef def) {
        super(world);
        this.body = body;
        this.def = def;
    }

    public CreateFixtureTask(World world, PhysicsService service, Body body, FixtureDef def) {
        super(world, service);
        this.body = body;
        this.def = def;
    }

    @Override
    public Fixture run() {
        int i = service.queueCreateFixture(body, def);
        Fixture fixture;
        while((fixture = service.obtainFixture(i)) == null) sleep(1);
        def.shape.dispose();
        return fixture;
    }

}
