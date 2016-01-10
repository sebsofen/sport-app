package sebastians.sportan.tasks.caches;

import android.support.v4.util.LruCache;

import java.util.ArrayList;

import sebastians.sportan.networking.SportActivity;

/**
 * Created by sebastian on 10/01/16.
 */
public class SportActivitiesCache {
    private static LruCache<String, SportActivity> lruCache = new LruCache<>(100);
    private static ArrayList<SportActivity> sportActivityArrayList = new ArrayList<>();
    public static void add(String id, SportActivity object) {
        lruCache.put(id,object);
    }
    public static SportActivity get(String id) {
        return lruCache.get(id);
    }
    public static void add(SportActivity sport) {
        lruCache.put(sport.id, sport);
    }

    public static ArrayList<SportActivity> getSportActivitiesList() {
        return sportActivityArrayList;
    }

    public static void setSportActivitiesList(ArrayList<SportActivity> sports) {
        sportActivityArrayList = sports;
    }
}
