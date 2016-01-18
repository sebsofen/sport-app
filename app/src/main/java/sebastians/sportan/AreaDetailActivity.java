package sebastians.sportan;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.customviews.LoadingView;
import sebastians.sportan.fragments.AreaDetailAdminFragment;
import sebastians.sportan.fragments.AreaDetailFragment;
import sebastians.sportan.fragments.CreateSportActivityFragment;
import sebastians.sportan.networking.ServiceConstants;
import sebastians.sportan.networking.SportActivity;
import sebastians.sportan.networking.SportActivitySvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class AreaDetailActivity extends AppCompatActivity implements CreateSportActivityFragment.CreateSportActivityListener {
    public static final String EXTRA_AREA_ID = "areaid";
    public static final String EXTRA_AREA_LAT = "arealat";
    public static final String EXTRA_AREA_LON = "arealon";

    String areaid;
    AreaDetailAdminFragment areaDetailAdminFragment;
    public LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //created from intent
        Intent intent = this.getIntent();
        areaid = intent.getStringExtra(AreaDetailActivity.EXTRA_AREA_ID);

        areaDetailAdminFragment = new AreaDetailAdminFragment();
        setContentView(R.layout.activity_area_detail);

        MyCredentials myCredentials = new MyCredentials(this);
        loadingView = (LoadingView) findViewById(R.id.loading_view);

        Switch toggleAdmin = (Switch) findViewById(R.id.adm_swtch);
        if(myCredentials.amIAdmin()) {
            Log.i("AreaDetailActivity", "This is the admin");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, areaDetailAdminFragment).commit();

            toggleAdmin.setChecked(true);
            toggleAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AreaDetailAdminFragment()).commit();
                    }else{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AreaDetailFragment()).commit();
                    }
                }
            });
        }else{
            toggleAdmin.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AreaDetailFragment()).commit();

        }





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * called by create activity fragment
     * activity will be transmitted here
     * @param sportActivity
     */
    @Override
    public void activityCreated(final SportActivity sportActivity) {
        Log.i("AreaDetailFragment", "called");
        sportActivity.setArea(areaid);
        final CustomAsyncTask createActivityTask = new CustomAsyncTask(AreaDetailActivity.this);
        createActivityTask.setTaskCallBacks(new TaskCallBacks() {
            @Override
            public String doInBackground() {
                TMultiplexedProtocol mp;
                try {
                    MyCredentials myCredentials = new MyCredentials(AreaDetailActivity.this);
                    mp = createActivityTask.openTransport(ServiceConstants.SERVICE_SPORTACTIVITY);
                    SportActivitySvc.Client client = new SportActivitySvc.Client(mp);
                    client.createActivity(myCredentials.getToken(),sportActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onPreExecute() {
                loadingView.startAnimation();
            }

            @Override
            public void onPostExecute() {
                loadingView.stopAnimation();
            }
        });
        createActivityTask.execute();


    }
}
