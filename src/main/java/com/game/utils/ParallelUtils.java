package com.game.utils;

import com.game.utils.log.LogUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadFactory;

public class ParallelUtils {
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() / 2;
    private static final int FORK_JOIN_POOL_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(
            THREAD_COUNT,
            new DaemonThreadFactory()
    );
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(
            FORK_JOIN_POOL_THREAD_COUNT, new DaemonThreadFactory.ForkJoinDaemonThreadFactory(), new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LogUtil.logError(e.getMessage(), e);
        }
    },
            false
    );

    public static <T> Future<T> call(Callable<T> callable) {
        return EXECUTOR_SERVICE.submit(callable);
    }

    public static Future<?> run(Runnable runnable) {
        return EXECUTOR_SERVICE.submit(runnable);
    }

    public static <T> T run(RecursiveTask<T> task) {
        return FORK_JOIN_POOL.invoke(task);
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            var thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }

        private static class ForkJoinDaemonThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
            @Override
            public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                var thread = new ForkJoinWorkerThread(pool) {
                };
                thread.setDaemon(true);
                return thread;
            }
        }

    }
}
