package sebastians.sportan;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import sebastians.sportan.adapters.SportListAdapter;
import sebastians.sportan.customviews.ButtonDialog;
import sebastians.sportan.graphics.RoundMarker;
import sebastians.sportan.layouts.OuterLayout;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.GetAreaTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;
import sebastians.sportan.tasks.SportListTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class SportSelectActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private OuterLayout mOuterLayout;
    private RelativeLayout mMainLayout;
    private Context mThis;
    private int counter = 0;
    protected HashMap<Marker,String> markerids = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areamapoverview);

        mMainLayout = (RelativeLayout) findViewById(R.id.sport_select_layout);
        getLayoutInflater().in
        final GridView sportListView = (GridView) findViewById(R.id.sport_list);
        Log.i("SportSelectActivity", "" + sportListView);
        final ArrayList<Sport> sportList = new ArrayList<>();
        final SportListAdapter sportListAdapter = new SportListAdapter(this,R.id.sport_select_layout,sportList);
        sportListView.setAdapter(sportListAdapter);
        mThis = this;

        SportListTask sportListTask = new SportListTask(mThis);
        //get last known location
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute("");
        sportListAdapter.setOuterLayout(mOuterLayout);





        final MapFragment map = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);






























        mMainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mOuterLayout.isMoving()) {
                    v.setTop(oldTop);
                    v.setBottom(oldBottom);
                    v.setLeft(oldLeft);
                    v.setRight(oldRight);
                }
            }
        });

    }




    @Override
    public void onMapReady(GoogleMap gMap) {
        final GoogleMap googleMap = gMap;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            gMap.moveCamera(center);
            gMap.animateCamera(zoom);
        }

        gMap.setMyLocationEnabled(true);
        gMap.setOnMarkerClickListener(this);
        gMap.setOnMapLongClickListener(this);


        final CustomAsyncTask markerTask = new CustomAsyncTask(this);
        markerTask.setTaskCallBacks(new TaskCallBacks() {
            ArrayList<String> areas = new ArrayList<>();
            @Override
            public String doInBackground() {
                TMultiplexedProtocol mp = null;
                try {
                    mp = markerTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                    AreaSvc.Client client = new AreaSvc.Client(mp);
                    //TODO remove this junk
                    areas.addAll(client.getAllAreasInCity("798a9d13-89f0-48d4-7a9b-5320237dbd11"));

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
                for(int i = 0; i < areas.size(); i++) {
                    GetAreaTask getAreaTask = new GetAreaTask(SportSelectActivity.this, areas.get(i), new GetTaskFinishCallBack<Area>() {
                        @Override
                        public void onFinished(Area area) {
                            Log.i("area", area.center.get(0) + "lon: " + area.center.get(1));
                            markerids.put(
                                    googleMap.addMarker(
                                            new MarkerOptions().position(new LatLng(area.center.get(1), area.center.get(0)))
                                                    .title(area.title)
                                                    .snippet(area.description)
                                                    .flat(true)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(RoundMarker.RoundMarker(255, 0, 0)))
                                    ), area.id);
                        }
                    });
                    getAreaTask.execute();
                }


            }
        });
        markerTask.execute("");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String areaid = markerids.get(marker);
        Intent intent = new Intent(this, AreaDetailActivity.class);
        intent.putExtra(AreaDetailActivity.EXTRA_AREA_ID, areaid);
        startActivity(intent);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(mThis, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        //open dialog! -> but only, if user is set as admin
        //TODO IFADMIN
        new ButtonDialog(this, "Create new area at this location", "Create","Abort", null, "do it") {
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
                Log.i("sportselectactivity", "abort");
            }

            @Override
            public void onNeutralButtonClick() {
                Log.i("sportselectactivity", "neutral");

            }
        };
    }

    @Override
    public void onClick(View v) {
        Log.i("OuterLayout", "CLicky Kalcky");
    }
}
