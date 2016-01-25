package sebastians.sportan.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import sebastians.sportan.R;
import sebastians.sportan.adapters.SportListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Image;
import sebastians.sportan.networking.ImageSvc;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.GetAreaTask;
import sebastians.sportan.tasks.GetImageTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;
import sebastians.sportan.tasks.SportListTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

/**
 * TODO MOVE IMPLEMENTATION FROM ACTIVITY TO HERE
 * Created by sebastian on 06/12/15.
 */
public class AreaDetailAdminFragment extends Fragment {
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
    String areaid;
    ArrayList<String> selectedSports = new ArrayList<>();

    public AreaDetailAdminFragment() {
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("AreaDetailActivity", "hiiI");
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Log.i("AreaDetailActivity", "new Picture");
                Uri selectedImageUri = data.getData();
                InputStream is = null;
                try {
                    is = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    area_bitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, false);

                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                this.setImgBitmap(area_bitmap);

            }
        }
    }

    public void setAreaId(String areaid) {
        this.areaid = areaid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        final View view = inflater.inflate(R.layout.fragment_area_edit, container, false);
        areaid_txt = (TextView) view.findViewById(R.id.areaid_txt);
        submit_btn = (Button) view.findViewById(R.id.submit_btn);
        img_btn = (Button) view.findViewById(R.id.img_btn);
        sport_list = (GridView) view.findViewById(R.id.sport_list);
        area_img = (ImageView) view.findViewById(R.id.area_img);
        title_edit = (EditText) view.findViewById(R.id.title_edit);
        description_edit= (EditText) view.findViewById(R.id.description_edit);
        final Activity mActivity = getActivity();
        myCredentials = new MyCredentials(mActivity);


        //created from intent
        Intent intent = mActivity.getIntent();

        if(intent.getStringExtra(EXTRA_AREA_ID) != null) {
            areaid = intent.getStringExtra(EXTRA_AREA_ID);
        }

        final double lat = intent.getDoubleExtra(EXTRA_AREA_LAT,Double.MAX_VALUE);
        final double lon = intent.getDoubleExtra(EXTRA_AREA_LON,Double.MAX_VALUE);


        final ArrayList<Sport> sportList = new ArrayList<>();

        final SportListAdapter sportListAdapter = new SportListAdapter(mActivity,R.id.sport_select_layout,sportList);
        sportListAdapter.setSelectedList(selectedSports);
        sport_list.setAdapter(sportListAdapter);

        SportListTask sportListTask = new SportListTask(mActivity);
        //get last known location
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute("");

        sport_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sportid = sportList.get(position).getId();
                if (!selectedSports.contains(sportid)) {
                    selectedSports.add(sportid);
                } else {
                    selectedSports.remove(sportid);
                }
                sportListAdapter.notifyDataSetChanged();


            }
        });


        if(areaid != null &&  !areaid.equals("")){
            areaid_txt.setText(areaid);
            GetAreaTask getAreaTask = new GetAreaTask(mActivity, areaid, new GetTaskFinishCallBack<Area>() {
                @Override
                public void onFinished(Area gArea) {
                    area = gArea;
                    if(area.getImageid() != null && !area.getImageid().equals("")){
                        GetImageTask imgTask = new GetImageTask(mActivity,area_img,area.getImageid());
                        imgTask.execute();
                    }

                    description_edit.setText(area.getDescription());
                    title_edit.setText(area.getTitle());
                    if (area.getSports() != null) {
                        ArrayList<String> asports = (ArrayList<String>) area.getSports();
                        for (int i = 0; i < asports.size(); i++) {
                            selectedSports.add(asports.get(i));
                        }
                        sportListAdapter.notifyDataSetChanged();



                    }
                }
            });
            getAreaTask.execute();
        }



        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(area == null) {
                    area = new Area();
                    ArrayList<Double> arr = new ArrayList<>();
                    arr.add(lon);
                    arr.add(lat);
                    area.setCenter(arr);
                    area.setCityid(MyCredentials.Me.getProfile().getCity_id());
                }
                //send information to server!
                area.setTitle(title_edit.getText().toString());
                area.setDescription(description_edit.getText().toString());

                area.setSports(selectedSports);




                final CustomAsyncTask submitNewAreaTask = new CustomAsyncTask(mActivity);
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
                                //getActivity().getFragmentManager().popBackStack();
                                getActivity().onBackPressed();

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

        return view;
    }

    public void setImgBitmap(Bitmap image){
        area_bitmap = image;
        area_img.setImageBitmap(area_bitmap);
    }

    public String getPath(Uri contentUri) {
        String res = "";
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        Log.i("AreaDetailActivity", "imagepath " +  res);
        return res;
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
