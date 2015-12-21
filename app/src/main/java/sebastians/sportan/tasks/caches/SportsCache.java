package sebastians.sportan.tasks.caches;

import android.support.v4.util.LruCache;

import java.util.ArrayList;

import sebastians.sportan.networking.Sport;

/**
 * Created by sebastian on 18/12/15.
 */
public class SportsCache {
    private static LruCache<String, Sport> lruCache = new LruCache<>(100);
    private static ArrayList<Sport> sportArrayList = new ArrayList<>();
    public static void add(String id, Sport object) {
        lruCache.put(id,object);
    }
    public static Sport get(String id) {
        return lruCache.get(id);
    }
    public static void add(Sport sport) {
        lruCache.put(sport.id, sport);
    }

    public static ArrayList<Sport> getSportsList() {
        return sportArrayList;
    }

    public static void setSportsList(ArrayList<Sport> sports) {
        sportArrayList = sports;
    }

}

