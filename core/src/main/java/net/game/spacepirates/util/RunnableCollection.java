package net.game.spacepirates.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunnableCollection implements Runnable {

    private final List<Runnable> tasks;

    public RunnableCollection(Runnable... tasks) {
        this.tasks = Arrays.asList(tasks);
    }

    public RunnableCollection() {
        this.tasks = new ArrayList<>();
    }

    public void add(Runnable task) {
        this.tasks.add(task);
    }

    public void remove(Runnable task) {
        this.tasks.remove(task);
    }

    @Override
    public void run() {
        tasks.forEach(Runnable::run);
    }
}
