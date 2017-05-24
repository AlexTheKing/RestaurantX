package com.example.alex.restaurantx.threads;

import com.example.alex.restaurantx.constants.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadManager {

    private static ThreadManager sThreadManager;
    private final ExecutorService mExecutorService;
    private final PriorityBlockingQueue<Runnable> mThreadQueue;

    private ThreadManager() {
        int mNumberOfThreads = Constants.ThreadManagerSettings.DEFAULT_NUMBER_OF_THREADS;

        if (Runtime.getRuntime().availableProcessors() > Constants.ThreadManagerSettings.DEFAULT_PROCESSORS_THRESHOLD) {
            mNumberOfThreads = Runtime.getRuntime().availableProcessors();
        }

        mThreadQueue = new PriorityBlockingQueue<>();
        this.mExecutorService = new ThreadPoolExecutor(mNumberOfThreads, mNumberOfThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, mThreadQueue);
    }

    public static ThreadManager getInstance() {
        if (sThreadManager == null) {
            sThreadManager = new ThreadManager();
        }

        return sThreadManager;
    }

    public <Params, Progress, Result> void execute(final PriorityRunnable<Params, Progress, Result> pRunnable) {
        mThreadQueue.put(pRunnable);
        mExecutorService.execute(pRunnable);
    }

}
