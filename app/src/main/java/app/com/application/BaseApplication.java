package app.com.application;

import android.app.Application;

import app.com.application.fetch.Fetch;

/**
 * Created by admin on 09/09/17.
 */

public class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        new Fetch.Settings(this)
                .setAllowedNetwork(Fetch.NETWORK_ALL)
                .enableLogging(true)
                .setConcurrentDownloadsLimit(1)
                .setFollowSslRedirects(true)
                .apply();

    }
}
