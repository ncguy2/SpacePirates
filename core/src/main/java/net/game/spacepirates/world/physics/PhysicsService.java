package net.game.spacepirates.world.physics;

import com.badlogic.gdx.physics.box2d.*;
import net.game.spacepirates.services.BaseService;

import java.util.ArrayList;
import java.util.List;

public abstract class PhysicsService extends BaseService {

    protected final List<Runnable> taskList = new ArrayList<>();

    public synchronized void queueTask(Runnable task) {
        taskList.add(task);
    }

    public abstract int queueCreateBody(BodyDef def);
    public abstract int queueCreateFixture(Body body, FixtureDef def);
    public abstract int queueCreateFixtures(Body body, FixtureDef... defs);
    public abstract int queueCreateJoint(JointDef def);

    public abstract Body obtainBody(int id);
    public abstract Fixture obtainFixture(int id);
    public abstract Fixture[] obtainFixtures(int id);
    public abstract Joint obtainJoint(int id);

    public abstract void queueRemoveBody(Body body);
    public abstract void queueRemoveFixture(Body body, Fixture fixture);
    public abstract void queueRemoveFixtures(Body body, Fixture... fixtures);
    public abstract void queueRemoveJoint(Joint joint);

    public abstract void produce();
    public abstract void execute();
    public abstract void remove();

    @Override
    public String name() {
        return "Physics";
    }

    @Override
    public Class<? extends BaseService> getServiceClass() {
        return PhysicsService.class;
    }

}
