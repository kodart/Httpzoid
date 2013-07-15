package com.kodart.httpzoid;

import android.os.AsyncTask;

public class AsyncTaskCancellable implements Cancellable {
    private AsyncTask task;

    public AsyncTaskCancellable(AsyncTask task) {
        this.task = task;
    }

    public void cancel() {
        task.cancel(false);
    }
}
