package sebastians.sportan.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sebastians.sportan.AreaDetailActivity;
import sebastians.sportan.R;
import sebastians.sportan.adapters.SportListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.customviews.ButtonDialog;
import sebastians.sportan.graphics.RoundMarker;
import sebastians.sportan.layouts.OuterLayout;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Coordinate;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.GetAreaTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;
import sebastians.sportan.tasks.SportListTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

/**
 * Created by sebastian on 11/12/15.
 */
public class MainMapFragment extends Fragment implements View.OnClickListener,OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private OuterLayout mapLayout;
    private RelativeLayout sportSelectLayout;
    private Context mThis;
    protected HashMap<Marker,String> markerids = new HashMap<>();
    private MyCredentials myCredentials;
    public static MainMapFragment newInstance() {
        MainMapFragment fragment = new MainMapFragment();
        return fragment;
    }

    public MainMapFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_areamapoverview, container, false);
        mThis = getActivity();
        myCredentials = new MyCredentials(getActivity());
        mapLayout = (OuterLayout) view.findViewById(R.id.outer_layout);
        sportSelectLayout = (RelativeLayout) view.findViewById(R.id.sport_select_layout);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.list_refresh);
        final GridView sportListView = (GridView) view.findViewById(R.id.sport_list);

        final ArrayList<Sport> sportList = new ArrayList<>();
        final SportListAdapter sportListAdapter = new SportListAdapter(mThis,R.id.sport_select_layout,sportList);
        sportListView.setAdapter(sportListAdapter);

        SportListTask sportListTask = new SportListTask(mThis);
        sportListTask.setConnectedRefreshLayout(refreshLayout);
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute();

        final MapFragment map = (MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        return view;
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onMapReady(GoogleMap gMap){
    final GoogleMap googleMap = gMap;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        final double lat = location.getLatitude();
        final double lon = location.getLongitude();
        gMap.setMyLocationEnabled(true);
        if(location != null) {
            Log.i("MainMap", "location not null");
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            gMap.moveCamera(center);
            gMap.animateCamera(zoom);
        }else{
            Log.i("MainMap", "location  null");
        }


    gMap.setOnMarkerClickListener(this);
    gMap.setOnMapLongClickListener(this);


    final CustomAsyncTask markerTask = new CustomAsyncTask(mThis);
    markerTask.setTaskCallBacks(new TaskCallBacks() {
        ArrayList<String> areas = new ArrayList<>();
        @Override
        public String doInBackground() {
            try {
                TMultiplexedProtocol mp = markerTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                AreaSvc.Client client = new AreaSvc.Client(mp);
                areas.clear();
                areas.addAll(client.getNearBy(myCredentials.getToken(),new Coordinate(lat,lon),250));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPreExecute() {}
        @Override
        public void onPostExecute() {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            for(int i = 0; i < (areas.size() > 250 ? 250 : areas.size()) ; i++) {
                GetAreaTask getAreaTask = new GetAreaTask(getActivity(), areas.get(i), new GetTaskFinishCallBack<Area>() {
                    @Override
                    public void onFinished(Area area) {
                        if(area != null) {
                            int j = 0;
                            for(int i = 0; i < areas.size(); i++){
                                if(areas.get(i).equals(area.id)) {
                                    j = i;
                                    break;
                                }
                            }
                        markerids.put(
                                googleMap.addMarker(
                                        new MarkerOptions().position(new LatLng(area.center.get(1), area.center.get(0)))
                                                .title(area.title)
                                                .snippet(area.description)
                                                .flat(true)
                                                .icon(BitmapDescriptorFactory.fromBitmap(RoundMarker.RoundMarker(j,j,j)))
                                ), area.id);

                        }
                    }
                });
                getAreaTask.executeOnExecutor(executor);
            }

        }
    });
    markerTask.execute();
}

    @Override
    public boolean onMarkerClick(Marker marker) {
        String areaid = markerids.get(marker);
        Intent intent = new Intent(mThis, AreaDetailActivity.class);
        intent.putExtra(AreaDetailActivity.EXTRA_AREA_ID, areaid);
        startActivity(intent);
        return true;
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        //open dialog! -> but only, if user is set as admin
        if(myCredentials.amIAdmin()) {
            new ButtonDialog(mThis, "Create new area at this location", "Create", "Abort", null, "do it") {
                @Override
                public void onPositiveButtonClick() {
                    Log.i("sportselectactivity", "create");
                    //start intent for new area
                    Intent intent = new Intent(mThis, AreaDetailActivity.class);
                    intent.putExtra(AreaDetailActivity.EXTRA_AREA_LAT, latLng.latitude);
                    intent.putExtra(AreaDetailActivity.EXTRA_AREA_LON, latLng.longitude);
                    startActivity(intent);
                }
                @Override
                public void onNegativeButtonClick() {
                }
                @Override
                public void onNeutralButtonClick() {
                }
            };
        }
    }


}
