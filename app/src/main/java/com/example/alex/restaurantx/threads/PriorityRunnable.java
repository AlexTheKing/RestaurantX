package com.example.alex.restaurantx.threads;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.example.alex.restaurantx.callbacks.IResultCallback;

public class PriorityRunnable<Params, Progress, Result> implements Runnable, Comparable<PriorityRunnable> {

    private final ITask<Params, Progress, Result> mTask;
    private final Params mParams;
    private final IProgressCallback<Progress> mProgressCallback;
    private final IResultCallback<Result> mResultCallback;
    private final Handler mHandler;
    private final IProgressCallback<Progress> mTaskProgressCallback = new IProgressCallback<Progress>() {

        @Override
        public void onProgressUpdate(final Progress pProgress) {
            if (mHandler != null) {
                mHandler.post(getHandlerPostProgressRunnable(pProgress));
            }
        }
    };
    private int mPriority;

    public PriorityRunnable(final Handler pHandler, final ITask<Params, Progress, Result> pTask, final IResultCallback<Result> pResultCallback) {
        this(pHandler, pTask, null, null, pResultCallback);
    }

    public PriorityRunnable(final Handler pHandler, final ITask<Params, Progress, Result> pTask, final Params pParams, final IProgressCallback<Progress> pProgressCallback, final IResultCallback<Result> pResultCallback) {
        mHandler = pHandler;
        mTask = pTask;
        mParams = pParams;
        mProgressCallback = pProgressCallback;
        mResultCallback = pResultCallback;
        mPriority = Thread.MAX_PRIORITY;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(final int pPriority) {
        mPriority = pPriority;
    }

    @Override
    public void run() {
        try {
            final Result result = mTask.doInBackground(mParams, mTaskProgressCallback);

            if (mHandler != null) {
                mHandler.post(getHandlerPostResultRunnable(result));
            }
        } catch (final Exception e) {
            if (mHandler != null) {
                mHandler.post(getHandlerPostErrorRunnable(e));
            }
        }
    }

    @Override
    public int compareTo(@NonNull final PriorityRunnable pRunnable) {
        final int otherRunnablePriority = pRunnable.getPriority();
        //Because PriorityBlockingQueue places less objects to the up of the queue
        //So they are executed earlier
        if (mPriority > otherRunnablePriority) {
            return -1;
        } else if (mPriority == otherRunnablePriority) {
            return 0;
        } else {
            return 1;
        }
    }

    private Runnable getHandlerPostProgressRunnable(final Progress pProgress) {
        return new Runnable() {

            @Override
            public void run() {
                if (mProgressCallback != null) {
                    mProgressCallback.onProgressUpdate(pProgress);
                }
            }
        };
    }

    private Runnable getHandlerPostResultRunnable(final Result pResult) {
        return new Runnable() {

            @Override
            public void run() {
                if (mResultCallback != null) {
                    mResultCallback.onSuccess(pResult);
                }
            }
        };
    }

    private Runnable getHandlerPostErrorRunnable(final Exception pException) {
        return new Runnable() {

            @Override
            public void run() {
                if (mResultCallback != null) {
                    mResultCallback.onError(pException);
                }
            }
        };
    }
}
