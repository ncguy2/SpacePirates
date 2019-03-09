package net.game.spacepirates.world.physics.workers;

import java.util.function.Consumer;

public abstract class ThreadTask<T, SELF extends ThreadTask> {

    protected float currentProgress;
    protected State currentState;
    protected Consumer<T> onFinish;

    public ThreadTask() {
        this.currentState = State.Pending;
    }

    public float progress() {
        return currentProgress;
    }

    public void progress(float progress) {
        this.currentProgress = progress;
    }

    public void sleep(int millis) {
        try{
            Thread.sleep(millis);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ThreadTask<T, SELF> setOnFinish(Consumer<T> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    protected void onFinish(T data) {
        if(this.onFinish != null) {
            this.onFinish.accept(data);
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public void execute() {
        start();
        T data = run();
        finish();
        onFinish(data);
    }

    public void start() {
        this.currentState = State.Running;
    }

    public void finish() {
        this.currentState = State.Completed;
    }

    public abstract T run();

    public enum State {
        Pending,
        Running,
        Completed
    }
}
