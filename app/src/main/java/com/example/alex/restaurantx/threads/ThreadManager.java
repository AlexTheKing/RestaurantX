package com.example.alex.restaurantx.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {

    private static ThreadManager sThreadManager;
    private int mNumberOfThreads = 3;
    private final ExecutorService mExecutorService;
    private final PriorityBlockingQueue<Runnable> mThreadQueue;

    private ThreadManager() {
        if (Runtime.getRuntime().availableProcessors() > 2) {
            mNumberOfThreads = Runtime.getRuntime().availableProcessors();
        }
        mThreadQueue = new PriorityBlockingQueue<>();
        this.mExecutorService = new ThreadPoolExecutor(mNumberOfThreads, mNumberOfThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, mThreadQueue);
//        this.mExecutorService = Executors.newFixedThreadPool(mNumberOfThreads);
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
