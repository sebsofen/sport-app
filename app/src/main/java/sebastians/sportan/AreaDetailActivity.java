package sebastians.sportan;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sebastians.sportan.adapters.SportListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Image;
import sebastians.sportan.networking.ImageSvc;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.GetImageTask;
import sebastians.sportan.tasks.SportListTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class AreaDetailActivity extends ActionBarActivity {
    private static final int SELECT_PICTURE = 1;
    public static final String EXTRA_AREA_ID = "areaid";
    public static final String EXTRA_AREA_LAT = "arealat";
    public static final String EXTRA_AREA_LON = "arealon";
    TextView areaid_txt;
    Button submit_btn;
    Button img_btn;
    ImageView area_img;
    GridView sport_list;
    EditText title_edit;
    EditText description_edit;
    public MyCredentials myCredentials;
    Bitmap area_bitmap;
    Area area;

    HashMap<String,Integer> selectedSports = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);
        areaid_txt = (TextView) findViewById(R.id.areaid_txt);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        img_btn = (Button) findViewById(R.id.img_btn);
        sport_list = (GridView) findViewById(R.id.sport_list);
        area_img = (ImageView) findViewById(R.id.area_img);
        title_edit = (EditText) findViewById(R.id.title_edit);
        description_edit= (EditText) findViewById(R.id.description_edit);

        myCredentials = new MyCredentials(this);
        //created from intent
        Intent intent = getIntent();
        final String areaid = intent.getStringExtra(AreaDetailActivity.EXTRA_AREA_ID);

        if(areaid == null || areaid.equals("")){
            final double lat = intent.getDoubleExtra(AreaDetailActivity.EXTRA_AREA_LAT,Double.MAX_VALUE);
            final double lon = intent.getDoubleExtra(AreaDetailActivity.EXTRA_AREA_LON,Double.MAX_VALUE);
        }


        final ArrayList<Sport> sportList = new ArrayList<>();

        final SportListAdapter sportListAdapter = new SportListAdapter(this,R.id.sport_select_layout,sportList);
        sportListAdapter.setSelectedList(selectedSports);
        sport_list.setAdapter(sportListAdapter);
        final AreaDetailActivity mThis = this;

        SportListTask sportListTask = new SportListTask(mThis);
        //get last known location
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute("");

        sport_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sportid = sportList.get(position).getId();
                if (selectedSports.get(sportid) == null) {
                    selectedSports.put(sportid, 1);
                } else {
                    selectedSports.remove(sportid);
                }
                //TODO this can be reset to direct update in views (will improve performance)
                //TODO for this it is necessary to user recyclerview
                sportListAdapter.notifyDataSetChanged();


            }
        });


        areaid_txt.setText(areaid);
        if(areaid != null &&  !areaid.equals("")){
            final CustomAsyncTask gatherInformationTask = new CustomAsyncTask(this);
            gatherInformationTask.setTaskCallBacks(
                    new TaskCallBacks() {
                        Bitmap bMap;
                        @Override
                        public String doInBackground() {
                            TMultiplexedProtocol mp = null;
                            try {
                                mp = gatherInformationTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                                AreaSvc.Client client = new AreaSvc.Client(mp);
                                area = client.getAreaById(areaid);
                                gatherInformationTask.closeTransport();
                                if(area.getImageid() != null && !area.getImageid().equals("")){
                                    GetImageTask imgTask = new GetImageTask(mThis,area_img,area.getImageid());
                                    imgTask.execute();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        public void onPreExecute() {

                        }

                        @Override
                        public void onPostExecute() {
                            description_edit.setText(area.getDescription());
                            title_edit.setText(area.getTitle());
                            if (area.getSports() != null) {
                                ArrayList<String> asports = (ArrayList<String>) area.getSports();
                                for (int i = 0; i < asports.size(); i++) {
                                    selectedSports.put(asports.get(i), 1);
                                }
                                sportListAdapter.notifyDataSetChanged();



                            }

                        }
                    }
            );
            gatherInformationTask.execute("");
        }


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send information to server!
                area.setTitle(title_edit.getText().toString());
                area.setDescription(description_edit.getText().toString());

                ArrayList<String> areaSportList = new ArrayList<String>();
                Iterator<String> it = selectedSports.keySet().iterator();
                while (it.hasNext()) {
                    areaSportList.add(it.next());
                }
                area.setSports(areaSportList);




                final CustomAsyncTask submitNewAreaTask = new CustomAsyncTask(mThis);
                submitNewAreaTask.setTaskCallBacks(
                        new TaskCallBacks() {
                            @Override
                            public String doInBackground() {
                                TMultiplexedProtocol mp = null;
                                try {
                                    //store image:
                                    if(area_bitmap != null){
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        area_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                                        byte[] byteArray = stream.toByteArray();
                                        Image sImage = new Image();
                                        sImage.bcontent = ByteBuffer.wrap(byteArray);
                                        mp = submitNewAreaTask.openTransport(SuperAsyncTask.SERVICE_IMAGE);
                                        ImageSvc.Client imgClient = new ImageSvc.Client(mp);
                                        area.setImageid(imgClient.createImage(myCredentials.getToken(), sImage));
                                        submitNewAreaTask.closeTransport();
                                    }

                                    mp = submitNewAreaTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                                    AreaSvc.Client client = new AreaSvc.Client(mp);

                                    client.updateArea(myCredentials.getToken(), area);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                return null;
                            }

                            @Override
                            public void onPreExecute() {
                            }

                            @Override
                            public void onPostExecute() {
                                mThis.finish();
                            }
                        }
                );

                submitNewAreaTask.execute("");


            }
        });


        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                area_bitmap = getScaledBitmap(getPath(selectedImageUri), 640, 640);
                area_img.setImageBitmap(area_bitmap);
                //selectedImagePath = getPath(selectedImageUri);
            }
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
