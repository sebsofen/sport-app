package sebastians.sportan.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;

import sebastians.sportan.R;
import sebastians.sportan.TabActivity;
import sebastians.sportan.fragments.MainFriendsFragment;
import sebastians.sportan.fragments.MainMapFragment;

/**
 * Created by sebastian on 11/12/15.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    private TabActivity context;
    private Toolbar toolbar;
    private String tabTitles[] = new String[] { "Map", "Friends"};
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public TabPagerAdapter(FragmentManager fm, TabActivity context) {
        super(fm);
        this.context = context;
        toolbar = (Toolbar) context.findViewById(R.id.toolbar);

    }
    @Override
    public Fragment getItem(int position) {
        toolbar.getMenu().clear();
        switch (position) {
            case 0:
                toolbar.inflateMenu(R.menu.menu_main);
                return MainMapFragment.newInstance();
            case 1:
                toolbar.inflateMenu(R.menu.menu_friends);
                return MainFriendsFragment.newInstance();
            default:
                return null;
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
