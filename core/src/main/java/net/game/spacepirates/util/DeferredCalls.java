package net.game.spacepirates.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeferredCalls {

    private static DeferredCalls instance;
    public static DeferredCalls get() {
        if(instance == null) {
            instance = new DeferredCalls();
        }
        return instance;
    }

    List<Call> calls;

    private DeferredCalls() {
        calls = new CopyOnWriteArrayList<>();
    }

    public void post(float delay, Runnable task) {
        post(new Call(delay, task));
    }

    public void post(Call call) {
        calls.add(call);
    }

    public void update(final float delta) {
        if(calls.isEmpty()) {
            return;
        }

        calls.forEach(c -> c.update(delta));
        calls.removeIf(Call::hasRun);
    }

    public static class Call {
        public float delay;
        public final Runnable task;
        public boolean hasRun;

        public Call(float delay, Runnable task) {
            this.delay = delay;
            this.task = task;
            this.hasRun = false;
        }

        public void update(float delta) {
            delay -= delta;
            if(delay <= 0) {
                run();
            }
        }

        public void run() {
            task.run();
            hasRun = true;
        }

        public boolean hasRun() {
            return hasRun;
        }
    }

}
