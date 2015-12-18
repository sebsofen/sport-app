package sebastians.sportan.tasks.caches;

import android.support.v4.util.LruCache;

import sebastians.sportan.networking.User;

/**
 * Created by sebastian on 18/12/15.
 */
public class UsersCache {
    private static LruCache<String, User> lruCache = new LruCache<>(100);

    public static void add(String id, User object) {
        lruCache.put(id,object);
    }

    public static User get(String id) {
        return lruCache.get(id);
    }
}
