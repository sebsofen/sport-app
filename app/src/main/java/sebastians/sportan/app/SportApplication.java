package sebastians.sportan.app;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Application Object
 * Access from everywhere in Android application
 * Great Place to store data that needs to be accessed from multiple places.
 */
public class SportApplication extends Application {


    public SportApplication(){
        super();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



}
