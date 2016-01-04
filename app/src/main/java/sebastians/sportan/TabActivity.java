package sebastians.sportan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import sebastians.sportan.adapters.TabPagerAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.customviews.LoadingView;

public class TabActivity extends AppCompatActivity {
    final TabActivity mThis = this;
    TabPagerAdapter tabPagerAdapter;
    MyCredentials myCredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        myCredentials = new MyCredentials(this);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        if(tabPagerAdapter == null)
            tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), TabActivity.this);
        viewPager.setAdapter(tabPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        LoadingView loadingView = (LoadingView) toolbar.findViewById(R.id.loading_view);
        // Give the TabLayout the ViewPager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                toolbar.getMenu().clear();
                switch (position) {
                    case 0:
                        toolbar.inflateMenu(R.menu.menu_main);
                        break;
                    case 1:
                        toolbar.inflateMenu(R.menu.menu_friends);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

        final String receivedUserId = getIntent().getStringExtra("USER");
        if(receivedUserId != null && !receivedUserId.equals("")) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        }



            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);







        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("toolbar", "opions");

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Log.i("Sportselect", "selected settings");
                Intent intent = new Intent(mThis, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.action_share_id:
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                String link = getResources().getString(R.string.webhost) + getResources().getString(R.string.apppref) + "users/" +  myCredentials.getIdentifier();
                intent2.putExtra(Intent.EXTRA_TEXT, "Hey Hey, Sport Informell: " + link);
                startActivity(Intent.createChooser(intent2, "Share via"));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}


