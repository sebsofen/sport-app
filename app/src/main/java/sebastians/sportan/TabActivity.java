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

public class TabActivity extends AppCompatActivity {
    final TabActivity mThis = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), TabActivity.this);
        viewPager.setAdapter(tabPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        // Give the TabLayout the ViewPager

        tabLayout.setupWithViewPager(viewPager);


        final String receivedUserId = getIntent().getStringExtra("USER");
        if(receivedUserId != null && !receivedUserId.equals("")) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);

            getSupportActionBar().setHomeButtonEnabled(true);

        }






        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i("Sportselect", "selected actionbarstuff");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //launch profile activity!

            Log.i("Sportselect", "selected settings");
            Intent intent = new Intent(mThis, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}


