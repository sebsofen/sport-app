package sebastians.sportan.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.AreaDetailActivity;
import sebastians.sportan.R;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.GetImageTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

/**
 * Created by sebastian on 06/12/15.
 */
public class AreaDetailFragment extends Fragment {
    MyCredentials myCredentials;
    ImageView area_img;
    TextView area_name_txt;
    TextView area_description_txt;
    Area area;
    public AreaDetailFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_area_detail, container, false);
        final Activity mActivity = getActivity();
        myCredentials = new MyCredentials(mActivity);


        //created from intent
        Intent intent = mActivity.getIntent();
        final String areaid = intent.getStringExtra(AreaDetailActivity.EXTRA_AREA_ID);

        if(areaid == null || areaid.equals("")){
            final double lat = intent.getDoubleExtra(AreaDetailActivity.EXTRA_AREA_LAT,Double.MAX_VALUE);
            final double lon = intent.getDoubleExtra(AreaDetailActivity.EXTRA_AREA_LON,Double.MAX_VALUE);
        }

        area_img = (ImageView) view.findViewById(R.id.area_img);
        area_name_txt = (TextView) view.findViewById(R.id.area_name_txt);
        area_description_txt = (TextView) view.findViewById(R.id.area_description_txt);
        //get image for area;
        if(areaid != null &&  !areaid.equals("")) {
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
                                if (area.getImageid() != null && !area.getImageid().equals("")) {
                                    GetImageTask imgTask = new GetImageTask(mActivity, area_img, area.getImageid());
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
                            area_name_txt.setText(area.getTitle());
                            area_description_txt.setText(area.getDescription());
                            /*
                            description_edit.setText(area.getDescription());
                            title_edit.setText(area.getTitle());
                            if (area.getSports() != null) {
                                ArrayList<String> asports = (ArrayList<String>) area.getSports();
                                for (int i = 0; i < asports.size(); i++) {
                                    selectedSports.put(asports.get(i), 1);
                                }
                                sportListAdapter.notifyDataSetChanged();



                            }
                               */
                        }
                    }
            );
            gatherInformationTask.execute("");

        }

        return view;
    }

}
