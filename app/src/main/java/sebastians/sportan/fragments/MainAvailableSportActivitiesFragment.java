package sebastians.sportan.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;

import sebastians.sportan.R;
import sebastians.sportan.adapters.SportActivitiesListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.ServiceConstants;
import sebastians.sportan.networking.SportActivitySvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

/**
 * Created by sebastian on 10/01/16.
 */
public class MainAvailableSportActivitiesFragment extends Fragment {
    SwipeRefreshLayout refreshLayout;
    ArrayList<String> sportActivitiesList = new ArrayList<>();
    SportActivitiesListAdapter sportActivitiesListAdapter;
    public static MainAvailableSportActivitiesFragment newInstance() {
        MainAvailableSportActivitiesFragment fragment = new MainAvailableSportActivitiesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //final MyCredentials myCredentials = new MyCredentials(getActivity(), this);
        View view = inflater.inflate(R.layout.fragment_main_sportactivities, container, false);

         sportActivitiesListAdapter = new SportActivitiesListAdapter(getActivity(),R.id.sportactivitieslist,sportActivitiesList);
        final ListView activitiesList = (ListView) view.findViewById(R.id.sportactivitieslist);
        activitiesList.setAdapter(sportActivitiesListAdapter);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);






        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewSportActivities();
            }
        });


        return view;
    }

    private void getNewSportActivities() {
        final CustomAsyncTask getActivityListTask = new CustomAsyncTask(getActivity());
        getActivityListTask.setTaskCallBacks(new TaskCallBacks() {
            ArrayList<String> actlist = new ArrayList<>();
            @Override
            public String doInBackground() {
                TMultiplexedProtocol mp;
                try {
                    MyCredentials myCredentials = new MyCredentials(MainAvailableSportActivitiesFragment.this.getActivity());
                    mp = getActivityListTask.openTransport(ServiceConstants.SERVICE_SPORTACTIVITY);
                    SportActivitySvc.Client client = new SportActivitySvc.Client(mp);
                    actlist.addAll(client.getAvailableActivityList(myCredentials.getToken()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onPreExecute() {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onPostExecute() {
                Log.i("MainAvailableSport", "lsit" + actlist.size());
                refreshLayout.setRefreshing(false);
                sportActivitiesList.clear();
                sportActivitiesList.addAll(actlist);
                sportActivitiesListAdapter.notifyDataSetChanged();

            }
        });
        getActivityListTask.execute();
    }


}
