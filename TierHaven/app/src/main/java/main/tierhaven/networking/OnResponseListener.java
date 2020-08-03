package main.tierhaven.networking;

public interface OnResponseListener<T> {
    void onResponse(T data);
}
