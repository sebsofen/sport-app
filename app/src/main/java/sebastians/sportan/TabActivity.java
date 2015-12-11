package sebastians.sportan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class TabActivity extends AppCompatActivity {
    final TabActivity mThis = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), TabActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);



            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View item) {
                    int id = item.getId();
                    Log.i("Sportselect", "selected actionbarstuff");
                    //noinspection SimplifiableIfStatement
                    if (id == R.id.action_settings) {
                        //launch profile activity!

                        Log.i("Sportselect", "selected settings");
                        Intent intent = new Intent(mThis, ProfileActivity.class);
                        startActivity(intent);
                    }

                }
            });


        }
    }

}
