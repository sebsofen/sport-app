package sebastians.sportan;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.customviews.LoadingView;
import sebastians.sportan.fragments.AreaDetailAdminFragment;
import sebastians.sportan.fragments.AreaDetailFragment;

public class AreaDetailActivity extends AppCompatActivity {
    public static final String EXTRA_AREA_ID = "areaid";
    public static final String EXTRA_AREA_LAT = "arealat";
    public static final String EXTRA_AREA_LON = "arealon";

    AreaDetailAdminFragment areaDetailAdminFragment;
    public LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        areaDetailAdminFragment = new AreaDetailAdminFragment();
        setContentView(R.layout.activity_area_detail);

        MyCredentials myCredentials = new MyCredentials(this);
        loadingView = (LoadingView) findViewById(R.id.loading_view);

        Switch toggleAdmin = (Switch) findViewById(R.id.adm_swtch);
        if(myCredentials.amIAdmin()) {
            Log.i("AreaDetailActivity", "This is the admin");
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, areaDetailAdminFragment).commit();

            toggleAdmin.setChecked(true);
            toggleAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AreaDetailAdminFragment()).commit();
                    }else{
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AreaDetailFragment()).commit();
                    }
                }
            });
        }else{
            toggleAdmin.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AreaDetailFragment()).commit();

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

}
