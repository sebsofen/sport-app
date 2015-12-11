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
    private String tabTitles[] = new String[] { "Map", "Tab2", "Tab3" };
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

                break;
            default:
                return PageFragment.newInstance(position + 1);
        }

        return PageFragment.newInstance(position + 1);
    }



    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
