package sebastians.sportan.app;

import android.app.Application;
import android.content.res.Configuration;
import android.util.LruCache;

import java.util.ArrayList;

import sebastians.sportan.networking.Image;
import sebastians.sportan.networking.Sport;

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

    /**
     * Cache Class for Sport application
     */
    public static class SportsCache {
        private static ArrayList<Sport> sportList = new ArrayList<>();


        public static ArrayList<Sport> getSportList(){
            return SportsCache.sportList;
        }

        public static void setSportList(ArrayList<Sport> sports){
            SportsCache.sportList = sports;
        }
    }

    /**
     * image cache
     * limit in cache has to be set wisely
     *
     * TODO ADD DISK CACHE IF POSSIBLE
     */
    public static class ImageCache {
        private static LruCache<String,Image> cache = new LruCache<>(100);

        public static void addImage(Image image){
            cache.put(image.getId(),image);
        }

        public static Image getImageById(String key) {
            return cache.get(key);
        }
    }

}
