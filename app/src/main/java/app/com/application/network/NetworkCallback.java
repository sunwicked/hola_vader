package app.com.application.network;

/**
 * Created by sunny on 8/8/16.
 */
public interface NetworkCallback<T> {

    void onResponse(T t, int requestType);
    void onFailure(Throwable t);
}
