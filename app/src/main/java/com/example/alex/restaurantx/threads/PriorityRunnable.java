package com.example.alex.restaurantx.threads;

import android.os.Handler;

import com.example.alex.restaurantx.callbacks.IResultCallback;

public class PriorityRunnable<Params, Progress, Result> implements Runnable, Comparable<PriorityRunnable> {

    private ITask<Params, Progress, Result> mTask;
    private Params mParams;
    private IProgressCallback<Progress> mProgressCallback;
    private IResultCallback<Result> mResultCallback;
    private Handler mHandler;
    private int mPriority;

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int pPriority) {
        mPriority = pPriority;
    }

    public PriorityRunnable(final Handler pHandler, final ITask<Params, Progress, Result> pTask, final Params pParams, final IProgressCallback<Progress> pProgressCallback, final IResultCallback<Result> pResultCallback) {
        mHandler = pHandler;
        mTask = pTask;
        mParams = pParams;
        mProgressCallback = pProgressCallback;
        mResultCallback = pResultCallback;
        mPriority = Thread.MAX_PRIORITY;
    }

    @Override
    public void run() {
        try {
            final Result result = mTask.doInBackground(mParams, new IProgressCallback<Progress>() {
                @Override
                public void onProgressUpdate(final Progress pProgress) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressCallback.onProgressUpdate(pProgress);
                        }
                    });
                }
            });
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResultCallback.onSuccess(result);
                }
            });
        } catch (final Exception e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResultCallback.onError(e);
                }
            });
        }
    }

    @Override
    public int compareTo(PriorityRunnable pRunnable) {
        int otherRunnablePriority = pRunnable.getPriority();
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

}
