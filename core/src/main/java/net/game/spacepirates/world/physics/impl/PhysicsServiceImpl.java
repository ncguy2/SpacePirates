package net.game.spacepirates.world.physics.impl;

import com.badlogic.gdx.physics.box2d.*;
import net.game.spacepirates.world.physics.PhysicsFactory;
import net.game.spacepirates.world.physics.PhysicsService;
import net.game.spacepirates.world.physics.data.BodyFixture;
import net.game.spacepirates.world.physics.data.BodyFixtureDef;
import net.game.spacepirates.world.physics.data.BodyFixturesDef;

import java.util.*;

public class PhysicsServiceImpl extends PhysicsService {

    protected int maxPerIteration = 16;
    protected World world;
    protected PhysicsFactory factory;
    protected final Map<Integer, Object> input;
    protected final Map<Integer, Object> output;
    protected final List<Object> recycleBin;
    protected int currentIdx = Integer.MIN_VALUE;

    public PhysicsServiceImpl(World world) {
        this.world = world;
        this.factory = new PhysicsFactoryImpl(world);
        input = new HashMap<>();
        output = new HashMap<>();
        recycleBin = new ArrayList<>();
    }

    public int nextId() {
        return currentIdx++;
    }

    @Override
    public synchronized int queueCreateBody(BodyDef def) {
        int id = nextId();
        input.put(id, def);
        return id;
    }

    @Override
    public synchronized int queueCreateFixture(Body body, FixtureDef def) {
        int id = nextId();
        input.put(id, new BodyFixtureDef(body, def));
        return id;
    }

    @Override
    public synchronized int queueCreateFixtures(Body body, FixtureDef... defs) {
        int id = nextId();
        input.put(id, new BodyFixturesDef(body, defs));
        return id;
    }

    @Override
    public synchronized int queueCreateJoint(JointDef def) {
        int id = nextId();
        input.put(id, def);
        return id;
    }

    @Override
    public synchronized Body obtainBody(int id) {
        return obtain(id, Body.class);
    }

    @Override
    public synchronized Fixture obtainFixture(int id) {
        return obtain(id, Fixture.class);
    }

    @Override
    public synchronized Fixture[] obtainFixtures(int id) {
        return obtain(id, Fixture[].class);
    }

    @Override
    public synchronized Joint obtainJoint(int id) {
        return obtain(id, Joint.class);
    }

    @Override
    public synchronized void queueRemoveBody(Body body) {
        recycleBin.add(body);
    }

    @Override
    public synchronized void queueRemoveFixture(Body body, Fixture fixture) {
        recycleBin.add(new BodyFixture(body, fixture));
    }

    @Override
    public synchronized void queueRemoveFixtures(Body body, Fixture... fixtures) {
        for (Fixture fixture : fixtures) {
            queueRemoveFixture(body, fixture);
        }
    }

    @Override
    public synchronized void queueRemoveJoint(Joint joint) {
        recycleBin.add(joint);
    }

    @Override
    public synchronized void produce() {
        Set<Integer> keys = input.keySet();
        if(keys.isEmpty()) {
            return;
        }
        int amt = 0;
        Iterator<Integer> it = keys.iterator();
        while(it.hasNext() && amt < maxPerIteration) {
            amt++;
            int id = it.next();
            Object obj = input.get(id);
            Object out = null;

            if(obj instanceof BodyDef) {
                out = factory.createBody((BodyDef) obj);
            }else if (obj instanceof JointDef) {
                out = factory.createJoint((JointDef) obj);
            } else if (obj instanceof BodyFixtureDef) {
                BodyFixtureDef def = (BodyFixtureDef) obj;
                out = factory.createFixture(def.body, def.definition);
            } else if (obj instanceof BodyFixturesDef) {
                BodyFixturesDef def = (BodyFixturesDef) obj;
                out = factory.createFixtures(def.body, def.definitions);
            }

            if(out != null) {
                output.put(id, out);
            }
        }

        output.keySet().forEach(input::remove);
    }

    @Override
    public void execute() {
        if(taskList.isEmpty()) {
            return;
        }

        Queue<Runnable> tasks;
        synchronized (taskList) {
            tasks = new LinkedList<>(taskList);
            taskList.clear();
        }

        while(!tasks.isEmpty()) {
            tasks.remove().run();
        }
    }

    @Override
    public void remove() {
        List<Object> bin;
        synchronized (recycleBin) {
            bin = new ArrayList<>(recycleBin);
            recycleBin.clear();
        }

        bin.forEach(obj -> {
            if (obj instanceof Body) {
                world.destroyBody((Body) obj);
            } else if (obj instanceof Joint) {
                world.destroyJoint((Joint) obj);
            } else if (obj instanceof BodyFixture) {
                BodyFixture def = (BodyFixture) obj;
                def.body.destroyFixture(def.fixture);
            }
        });
    }

    private <T> T obtain(int id, Class<T> type) {
        Object o = output.get(id);
        if(type.isInstance(o)) {
            output.remove(id);
            return type.cast(o);
        }
        return null;
    }

}
