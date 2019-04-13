package net.game.spacepirates.util;

import java.util.function.Consumer;

public class TaskUtils {

    public static void safeRun(ThrowableRunnable task) {
        safeRun(task, Exception::printStackTrace);
    }

    public static void safeRun(ThrowableRunnable task, Consumer<Exception> onException) {
        try {
            task.run();
        } catch (Exception e) {
            if (onException != null) {
                onException.accept(e);
            }
        }
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }

}
