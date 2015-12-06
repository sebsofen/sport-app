package sebastians.sportan.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sebastians.sportan.R;
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

    HashMap<String,Integer> selectedSports = new HashMap<>();

    public AreaDetailAdminFragment() {
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
        final String areaid = intent.getStringExtra(EXTRA_AREA_ID);

        if(areaid == null || areaid.equals("")){
            final double lat = intent.getDoubleExtra(EXTRA_AREA_LAT,Double.MAX_VALUE);
            final double lon = intent.getDoubleExtra(EXTRA_AREA_LON,Double.MAX_VALUE);
        }

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
            final CustomAsyncTask gatherInformationTask = new CustomAsyncTask(mActivity);
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
                                    GetImageTask imgTask = new GetImageTask(mActivity,area_img,area.getImageid());
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
                                //TODO This might be a strange behavoir
                                mActivity.finish();
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
}