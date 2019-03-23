package net.game.spacepirates.util;

public class TaskUtils {

    public static void safeRun(ThrowableRunnable task) {
        try{
            task.run();
        }catch (Exception e) {
            // TODO properly log
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }

}
