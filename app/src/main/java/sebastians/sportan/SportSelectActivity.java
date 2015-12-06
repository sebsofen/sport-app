package sebastians.sportan;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import sebastians.sportan.custviews.ButtonDialog;
import sebastians.sportan.graphics.RoundMarker;
import sebastians.sportan.layouts.OuterLayout;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SportListTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class SportSelectActivity extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {

    private OuterLayout mOuterLayout;
    private RelativeLayout mMainLayout;
    private Context mThis;
    protected HashMap<Marker,String> markerids = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areamapoverview);
        mOuterLayout = (OuterLayout) findViewById(R.id.outer_layout);
        mMainLayout = (RelativeLayout) findViewById(R.id.sport_select_layout);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_refresh);
        final GridView sportListView = (GridView) findViewById(R.id.sport_list);
        Log.i("SportSelectActivity", "" + sportListView);
        final ArrayList<Sport> sportList = new ArrayList<>();
        final SportListAdapter sportListAdapter = new SportListAdapter(this,R.id.sport_select_layout,sportList);
        sportListView.setAdapter(sportListAdapter);
        mThis = this;

        SportListTask sportListTask = new SportListTask(mThis);
        //get last known location
        sportListTask.setConnectedRefreshLayout(refreshLayout);
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute("");



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);

            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View item) {
                    int id = item.getId();
                    Log.i("Sportselect", "selected actionbarstuff");
                    //noinspection SimplifiableIfStatement
                    if (id == R.id.action_settings) {
                        //launch profile activity!

                        Log.i("Sportselect", "selected settings");
                        Intent intent = new Intent(mThis, ProfileActivity.class);
                        startActivity(intent);
                    }

                }
            });
        }



        final MapFragment map = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);































        /*

        Button mButten = (Button)mMainLayout.findViewById(R.id.button2);

        mButten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation anim= new TranslateAnimation(0, 0, 0, 200);
                anim.setDuration(200);
                anim.setFillAfter(false);
                mMainLayout.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mMainLayout.postInvalidateOnAnimation();
                        mMainLayout.clearAnimation();
                        mMainLayout.setTop(0);
                        mMainLayout.setTop(200);
                        mMainLayout.setBottom(mMainLayout.getBottom() + 200);
                        mMainLayout.setBottom(mMainLayout.getBottom() + 200);
                        //
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });
        */
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
    public void onClick(View v) {
        Button b = (Button) v;
        Toast t = Toast.makeText(this, b.getText() + " clicked", Toast.LENGTH_SHORT);
        t.show();
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
            ArrayList<Area> areas = new ArrayList<Area>();
            @Override
            public String doInBackground() {
                TMultiplexedProtocol mp = null;
                try {
                    mp = markerTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                    AreaSvc.Client client = new AreaSvc.Client(mp);
                    areas.addAll(client.getAllAreasInCity("798a9d13-89f0-48d4-7a9b-5320237dbd11"));
                    //areas.addAll(client.getAllAreasInCity("5d298a64-fed9-4d54-6ce9-3b78e24c1a82"));

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
                for(int i = 0; i < areas.size(); i++){
                    Log.i("area", "number" + i);
                    Area area = areas.get(i);
                    Log.i("area", area.center.lat + "lon: " + area.center.lon);
                    markerids.put(
                            googleMap.addMarker(
                                    new MarkerOptions().position(new LatLng(area.center.lat, area.center.lon))
                                            .title(area.title)
                                            .snippet(area.description)
                                            .flat(true)
                                            .icon(BitmapDescriptorFactory.fromBitmap(RoundMarker.RoundMarker(255, 0, 0)))

                            ), area.id);
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
        Log.i("Sportselect", "selected actionbarstuff");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //launch profile activity!

            Log.i("Sportselect", "selected settings");
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
}
