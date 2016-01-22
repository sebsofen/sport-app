package sebastians.sportan.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sebastians.sportan.AreaDetailActivity;
import sebastians.sportan.R;
import sebastians.sportan.adapters.SportListAdapter;
import sebastians.sportan.adapters.SportListSelectedFilter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.customviews.ButtonDialog;
import sebastians.sportan.customviews.LoadingView;
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
import sebastians.sportan.tasks.caches.AreasCache;

/**
 * Created by sebastian on 11/12/15.
 */
public class MainMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, LocationListener, SportListSelectedFilter {
    ExecutorService executor = Executors.newFixedThreadPool(25);

    private Context mThis;
    protected HashMap<Marker,String> markerids = new HashMap<>();
    protected HashMap<String,Marker> areamarkers = new HashMap<>();
    private MyCredentials myCredentials;
    private ArrayList<String> areas = new ArrayList<>();
    private boolean locationSet = false;
    public final long LOCATION_UPDATE_INTERVAL = 30 * 1000;
    public final float LOCATION_UPDATE_DISTANCE = 0.0f;
    SportListAdapter sportListAdapter;
    GoogleMap googleMap;
    ImageButton noFilterButton;
    LoadingView loadingView;
    RelativeLayout dragglayout;
    private boolean noFilter = true;

    AnimationDrawable map_arrow_animation;

    public static MainMapFragment newInstance() {
        MainMapFragment fragment = new MainMapFragment();
        return fragment;
    }

    public MainMapFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingView = (LoadingView)getActivity().findViewById(R.id.loading_view);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_areamapoverview, container, false);
        mThis = getActivity();
        myCredentials = new MyCredentials(getActivity());

        final GridView sportListView = (GridView) view.findViewById(R.id.sport_list);

        final ArrayList<Sport> sportList = new ArrayList<>();
        sportListAdapter = new SportListAdapter(mThis,R.id.sport_select_layout,sportList);


        dragglayout = (RelativeLayout) view.findViewById(R.id.dragglayout);

        final ImageButton dragbutton = (ImageButton)dragglayout.findViewById(R.id.dragg);


        if(android.os.Build.VERSION.SDK_INT >= 21){
            map_arrow_animation = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.animation_map_arrow,getActivity().getTheme());
        } else {
            map_arrow_animation = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.animation_map_arrow);
        }



        dragbutton.setImageDrawable(map_arrow_animation.getFrame(0));

        noFilterButton = (ImageButton) view.findViewById(R.id.no_filter_btn);
        noFilterButton.setVisibility(View.GONE);
        noFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainMapFragment.this.noFilter == false) {
                    MainMapFragment.this.noFilter = true;
                    sportListAdapter.resetFilter();
                    for(int i = 0; i < areas.size(); i++) {
                        final String areaid = areas.get(i);
                        Area area = AreasCache.get(areaid);
                        addToMap(area, null);
                    };
                }
                v.setVisibility(View.GONE);

            }
        });
        sportListView.setAdapter(sportListAdapter);
        sportListAdapter.setSportListSelectedFilter(this);
        SportListTask sportListTask = new SportListTask(mThis);
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute();

        final MapFragment map = (MapFragment)getActivity().getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        //sliding panel layout!
        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setDragView(R.id.dragg);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, final float slideOffset) {
                Log.i("slideOffset", slideOffset + "");

                    dragbutton.setImageDrawable(map_arrow_animation.getFrame((int) (14 * slideOffset)));

            }

            @Override
            public void onPanelCollapsed(View panel) {

            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
        sportListAdapter.setSlidingUpPanelLayout(slidingUpPanelLayout);

        return view;
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

    private void fetchAndDisplayMarkers(final Location loc) {
        final Location location = loc;
        if (googleMap == null)
            return;

        final CustomAsyncTask markerTask = new CustomAsyncTask(mThis);
        markerTask.setTaskCallBacks(new TaskCallBacks() {
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
            public void onPreExecute() {
                if(loadingView != null)
                    loadingView.startAnimation();
            }
            @Override
            public void onPostExecute() {
                if(loadingView != null)
                    loadingView.stopAnimation();
                //wait for next update, if execution is still running
                if(executor.isTerminated())
                    executor = Executors.newFixedThreadPool(25);
                else if(executor.isShutdown())
                    return;


                for(int i = 0; i < (areas.size()) ; i++) {
                    if(loadingView != null)
                        loadingView.startAnimation();
                    if(areamarkers.get(areas.get(i)) != null){
                        continue;
                    }


                    GetAreaTask getAreaTask = new GetAreaTask(getActivity(), areas.get(i), new GetTaskFinishCallBack<Area>() {
                        @Override
                        public void onFinished(Area area) {
                            if(loadingView != null)
                                loadingView.stopAnimation();

                            if(area != null) {
                                MainMapFragment.this.displayArea(MainMapFragment.this.sportListAdapter.getSelectedSportsList(),area);

                            }
                        }
                    });
                    getAreaTask.executeOnExecutor(executor);
                }
                executor.shutdown();
            }
        });
        markerTask.execute();
    }


    /**
     * Load new fragment to fragment placeholder
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        String areaid = markerids.get(marker);

        //if user is not admin:
        AreaDetailFragment areaDetailFragment = new AreaDetailFragment();
        areaDetailFragment.setAreaid(areaid);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_in);
        ft.addToBackStack(null);
        ft.replace(R.id.area_detail_fragment_placeholder, areaDetailFragment).commit();


        /*
        Intent intent = new Intent(mThis, AreaDetailActivity.class);

        intent.putExtra(AreaDetailActivity.EXTRA_AREA_ID, areaid);
        startActivity(intent);
        */
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




    @Override
    public void filterChanged(final ArrayList<String> filters) {
        noFilter = false;
        noFilterButton.setVisibility(View.VISIBLE);
        //display no filter icon!
        for(int i = 0; i < areas.size(); i++) {
            final String areaid = areas.get(i);
            Area area = AreasCache.get(areaid);
            displayArea(filters,area);
        };

    }

    public void displayArea(final ArrayList<String> mfilters, Area area) {
        if (area != null) {

            ArrayList<String> matchingSports = new ArrayList<>(mfilters);
            if (area.getSports() != null)
                matchingSports.retainAll(area.getSports());
            else
                matchingSports = new ArrayList<>();
            if (noFilter || matchingSports.size() > 0) {
                addToMap(area, mfilters);

            } else {
                removeFromMap(area.getId());
            }


        }
    }

    public synchronized void addToMap(Area area, ArrayList<String> filters){

        if(areamarkers.get(area.id) != null)
            return;

        Marker marker = googleMap.addMarker(
                new MarkerOptions().position(new LatLng(area.center.get(1), area.center.get(0)))
                        .title(area.title)
                        .snippet(area.description)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(RoundMarker.RoundMarker(0,255,0)))
        );
        markerids.put(marker, area.id);
        areamarkers.put(area.id,marker);

    }

    public synchronized void removeFromMap(String areaid) {

        if(areamarkers.get(areaid) != null){
            Marker marker = areamarkers.get(areaid);
            marker.remove();
            markerids.remove(marker);
            areamarkers.remove(areaid);
        }
    }

    public synchronized boolean isOnMap(String areaid) {
        return areamarkers.get(areaid) != null;
    }

    }

