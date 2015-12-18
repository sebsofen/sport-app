package sebastians.sportan;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.fragments.AreaDetailAdminFragment;
import sebastians.sportan.fragments.AreaDetailFragment;

public class AreaDetailActivity extends AppCompatActivity {
    public static final String EXTRA_AREA_ID = "areaid";
    public static final String EXTRA_AREA_LAT = "arealat";
    public static final String EXTRA_AREA_LON = "arealon";

    AreaDetailAdminFragment areaDetailAdminFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        areaDetailAdminFragment = new AreaDetailAdminFragment();
        setContentView(R.layout.activity_area_detail);

        MyCredentials myCredentials = new MyCredentials(this);

        //depending on user information, load admin fragement or user fragment
        //getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectCityFragment()).commit();
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










    }





    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
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
