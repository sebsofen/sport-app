package sebastians.sportan.tasks.caches;

import android.support.v4.util.LruCache;

import sebastians.sportan.networking.Area;

/**
 * Created by sebastian on 18/12/15.
 */
public class AreasCache {
    private static LruCache<String, Area> lruCache = new LruCache<>(1000);

    public static void add(String id, Area object) {
        lruCache.put(id,object);
    }

    public static Area get(String id) {
        return lruCache.get(id);
    }
}
