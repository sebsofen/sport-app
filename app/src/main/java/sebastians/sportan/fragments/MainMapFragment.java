package sebastians.sportan.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
public class MainMapFragment extends Fragment implements View.OnClickListener,OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, LocationListener {
    private Context mThis;
    protected HashMap<Marker,String> markerids = new HashMap<>();
    protected HashMap<String,Marker> areamarkers = new HashMap<>();
    private MyCredentials myCredentials;
    private boolean locationSet = false;
    public final long LOCATION_UPDATE_INTERVAL = 30 * 1000;
    public final float LOCATION_UPDATE_DISTANCE = 0.0f;
    GoogleMap googleMap;
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

        final GridView sportListView = (GridView) view.findViewById(R.id.sport_list);

        final ArrayList<Sport> sportList = new ArrayList<>();
        final SportListAdapter sportListAdapter = new SportListAdapter(mThis,R.id.sport_select_layout,sportList);
        sportListView.setAdapter(sportListAdapter);

        SportListTask sportListTask = new SportListTask(mThis);
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute();

        final MapFragment map = (MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        //sliding panel layout!
        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setDragView(R.id.dragg);
        sportListAdapter.setSlidingUpPanelLayout(slidingUpPanelLayout);

        return view;
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onMapReady(GoogleMap gMap){
     googleMap = gMap;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, false);
        Log.i("onMapReady", "provider"  + provider);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location != null) {
            Log.i("onMapReady", "lat "+ location.getLatitude() + "lon " + location.getLongitude());
            this.onLocationChanged(location);
        }else{
            Log.i("onMapReady", "location not provided");
        }


        gMap.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(provider,LOCATION_UPDATE_INTERVAL,LOCATION_UPDATE_DISTANCE,this);




        gMap.setOnMarkerClickListener(this);
        gMap.setOnMapLongClickListener(this);



}

    private void fetchAndDisplayMarkers(Location loc) {
        final Location location = loc;
        if (googleMap == null)
            return;

        final CustomAsyncTask markerTask = new CustomAsyncTask(mThis);
        markerTask.setTaskCallBacks(new TaskCallBacks() {
            ArrayList<String> areas = new ArrayList<>();
            @Override
            public String doInBackground() {
                try {
                    TMultiplexedProtocol mp = markerTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                    AreaSvc.Client client = new AreaSvc.Client(mp);
                    areas.clear();
                    areas.addAll(client.getNearBy(myCredentials.getToken(),new Coordinate(location.getLatitude(),location.getLongitude()),250));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onPreExecute() {}
            @Override
            public void onPostExecute() {
                ExecutorService executor = Executors.newFixedThreadPool(1);
                for(int i = 0; i < (areas.size() > 250 ? 250 : areas.size()) ; i++) {
                    if(areamarkers.get(areas.get(i)) != null){
                        continue;
                    }

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
                                Marker marker = googleMap.addMarker(
                                        new MarkerOptions().position(new LatLng(area.center.get(1), area.center.get(0)))
                                                .title(area.title)
                                                .snippet(area.description)
                                                .flat(true)
                                                .icon(BitmapDescriptorFactory.fromBitmap(RoundMarker.RoundMarker(j,j,j)))
                                );
                                markerids.put(marker, area.id);
                                areamarkers.put(area.id,marker);

                            }
                        }
                    });
                    getAreaTask.executeOnExecutor(executor);
                }

                //remove obsolete markers from map
                //there is a race condition here, when the tasks are still executing
                /*
                Iterator it = markerids.entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    Marker marker = (Marker) pair.getKey();
                    String areaid = (String) pair.getValue();
                    if(!areas.contains(areaid)){
                        marker.remove();
                        markerids.remove(marker);
                        areamarkers.remove(areaid);
                    }
                }
                */

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
        return false;
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


    @Override
    public void onLocationChanged(Location location) {
        Log.i("onLocationChanged", "New Location");
        fetchAndDisplayMarkers(location);
        if(!locationSet) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
            locationSet = true;
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
