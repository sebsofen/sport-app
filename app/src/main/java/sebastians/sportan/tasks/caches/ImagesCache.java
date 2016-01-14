package sebastians.sportan.tasks.caches;

import android.support.v4.util.LruCache;

import sebastians.sportan.networking.Image;

/**
 * Created by sebastian on 18/12/15.
 */
public class ImagesCache {
    private static LruCache<String, Image> lruCache = new LruCache<>(1000);

    public static void add(String id, Image object) {
        lruCache.put(id,object);
    }

    public static Image get(String id) {
        if(id == null)
            return null;

        return lruCache.get(id);
    }
}
