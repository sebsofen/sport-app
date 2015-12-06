package sebastians.sportan.fragments;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import sebastians.sportan.R;
import sebastians.sportan.adapters.AreaListAdapter;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.Coordinate;
import sebastians.sportan.tasks.AreaListTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class AreaListActivityFragment extends Fragment {

    public AreaListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_area_list, container, false);



        //connect listview to adapter and task!
        final ListView areaListView = (ListView) view.findViewById(R.id.area_list);
        final ArrayList<Area> areaArrayList = new ArrayList<Area>();
        final AreaListAdapter areaListAdapter = new AreaListAdapter(this.getActivity(),R.id.area_list,areaArrayList);
        areaListView.setAdapter(areaListAdapter);

        final AreaListActivityFragment myFragement = this;


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.area_list_refresh);



        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        AreaListTask areaListTask = new AreaListTask(myFragement.getActivity());
                        //get last known location
                        LocationManager locationManager = (LocationManager)myFragement.getActivity().getSystemService(myFragement.getActivity().LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Coordinate coord = new Coordinate(location.getLatitude(),location.getLongitude());
                            areaListTask.setCoordinate(coord);
                        }

                        areaListTask.setConnectedRefreshLayout(swipeRefreshLayout);
                        areaListTask.setConnectedAdapter(areaListAdapter);
                        areaListTask.connectArrayList(areaArrayList);
                        areaListTask.execute("");
                    }
                });




        return view;
    }
}
