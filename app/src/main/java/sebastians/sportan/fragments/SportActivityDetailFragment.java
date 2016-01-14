package sebastians.sportan.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.R;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.ServiceConstants;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.networking.SportActivity;
import sebastians.sportan.networking.SportActivitySvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.GetAreaTask;
import sebastians.sportan.tasks.GetImageTask;
import sebastians.sportan.tasks.GetSportActivityTask;
import sebastians.sportan.tasks.GetSportTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;
import sebastians.sportan.tasks.SvgImageTask;
import sebastians.sportan.tasks.TaskCallBacks;

/**
 * Created by sebastian on 11/01/16.
 */
public class SportActivityDetailFragment extends Fragment {
    Button decline_btn;
    Button join_btn;
    Button back_btn;

    TextView description_txt;
    ImageView sport_icon;
    ImageView area_img;
    String activityid = "";
    Toolbar toolbar;
    GetAreaTask getAreaTask;
    GetSportActivityTask getSportActivityTask;
    GetSportTask getSportTask;
    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }

    public SportActivityDetailFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sportactivitydetail, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("__");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);


        description_txt = (TextView) view.findViewById(R.id.description_txt);
        sport_icon = (ImageView) view.findViewById(R.id.sport_icon);
        area_img = (ImageView) view.findViewById(R.id.area_img);

        join_btn = (Button) view.findViewById(R.id.join_btn);
        decline_btn = (Button) view.findViewById(R.id.decline_btn);
        back_btn = (Button) view.findViewById(R.id.back_btn);

        getSportActivityTask = new GetSportActivityTask(getActivity(), activityid, new GetTaskFinishCallBack<SportActivity>() {
            @Override
            public void onFinished(SportActivity sportActivity) {
                if(sportActivity == null)
                    return;

                if(sportActivity.getDescription() != null)
                    description_txt.setText(sportActivity.getDescription());

                getSportTask = new GetSportTask(getActivity(), sportActivity.getSport(), new GetTaskFinishCallBack<Sport>() {
                    @Override
                    public void onFinished(Sport sport) {
                        if(sport == null)
                            return;
                        SvgImageTask sportIconTask = new SvgImageTask(getActivity());
                        sportIconTask.setImageView(sport_icon);
                        sportIconTask.execute(sport.getIconid());
                    }
                });
                getAreaTask = new GetAreaTask(getActivity(), sportActivity.getArea(), new GetTaskFinishCallBack<Area>() {
                    @Override
                    public void onFinished(Area area) {
                        if(area == null)
                            return;
                        toolbar.setTitle(area.getTitle());
                        GetImageTask areaImageTask = new GetImageTask(getActivity(),area_img,area.getImageid());
                        areaImageTask.execute();
                    }
                });
                getAreaTask.execute();
            }
        });
        getSportActivityTask.execute();



        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                final CustomAsyncTask joinTask = new CustomAsyncTask(getActivity());
                joinTask.setTaskCallBacks(new TaskCallBacks() {
                    @Override
                    public String doInBackground() {

                        TMultiplexedProtocol mp;
                        try {
                            MyCredentials myCredentials = new MyCredentials(getActivity());
                            mp = joinTask.openTransport(ServiceConstants.SERVICE_SPORTACTIVITY);
                            SportActivitySvc.Client client = new SportActivitySvc.Client(mp);
                            client.joinActivity(myCredentials.getToken(), activityid);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPreExecute() {
                        join_btn.setEnabled(false);
                        decline_btn.setEnabled(false);
                    }
                    @Override
                    public void onPostExecute() {
                        goBack();
                    }
                });
                joinTask.execute();
            }
        });

        decline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomAsyncTask declineTask = new CustomAsyncTask(getActivity());
                declineTask.setTaskCallBacks(new TaskCallBacks() {
                    @Override
                    public String doInBackground() {

                        TMultiplexedProtocol mp;
                        try {
                            MyCredentials myCredentials = new MyCredentials(getActivity());
                            mp = declineTask.openTransport(ServiceConstants.SERVICE_SPORTACTIVITY);
                            SportActivitySvc.Client client = new SportActivitySvc.Client(mp);
                            client.declineActivity(myCredentials.getToken(), activityid);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPreExecute() {
                        join_btn.setEnabled(false);
                        decline_btn.setEnabled(false);
                    }
                    @Override
                    public void onPostExecute() {
                        goBack();
                    }
                });
                declineTask.execute();
            }
        });










        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goBack();
            }
        });

        return view;
    }


    /**
     * go back to calling fragment
     */
    public void goBack() {
        toolbar.setNavigationIcon(null);
        toolbar.setTitle("Sportapp");

        if(getSportActivityTask != null)
            getSportActivityTask.cancel(true);

        if(getAreaTask != null)
            getAreaTask.cancel(true);

        getActivity().onBackPressed();
    }
}
