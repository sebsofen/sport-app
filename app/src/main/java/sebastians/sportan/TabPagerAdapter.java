package sebastians.sportan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sebastian on 11/12/15.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private String tabTitles[] = new String[] { "Map", "Friends"};
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainMapFragment.newInstance();
            case 1:
                return MainFriendsFragment.newInstance();
            default:
                return PageFragment.newInstance(position + 1);
        }
    }



    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
