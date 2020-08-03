package main.tierhaven.networking;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

public class HttpRequestTask extends AsyncTask<HttpRequest, Void, Either<Exception, HttpResponse>> {

    private boolean hasExecuted;

    private OnErrorListener onErrorListener;
    private OnResponseListener<HttpResponse> onResponseListener;

    public HttpRequestTask() {
        hasExecuted = false;
        onErrorListener = new OnErrorListener() {
            @Override
            public void onError(Exception error) {
                Log.d("DEBUG", error.getMessage());
            }
        };
    }

    public HttpRequestTask setOnErrorListener(OnErrorListener onErrorListener)  {
        if(hasExecuted)
            throw new IllegalStateException("Listeners should be set before executing task.");
        this.onErrorListener = onErrorListener;
        return this;
    }

    public HttpRequestTask setOnResponseListener(OnResponseListener<HttpResponse> onResponseListener) {
        if(hasExecuted)
            throw new IllegalStateException("Listeners should be set before executing task.");
        this.onResponseListener = onResponseListener;
        return this;
    }

    @Override
    protected Either<Exception, HttpResponse> doInBackground(HttpRequest... httpRequests) {
        hasExecuted = true;
        HttpRequest request = httpRequests[0];
        try {
            return Either.right(request.perform());
        } catch (IOException e) {
            return Either.left(e);
        }
    }

    @Override
    protected void onPostExecute(Either<Exception, HttpResponse> either) {
        switch (either.getType()) {
            case LEFT:
                if(onErrorListener != null)
                    onErrorListener.onError(either.getLeft());
                break;
            case RIGHT:
                if(onResponseListener != null)
                    onResponseListener.onResponse(either.getRight());
                break;
        }
    }
}
