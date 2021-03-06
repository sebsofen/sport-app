package sebastians.sportan.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;

import sebastians.sportan.R;
import sebastians.sportan.adapters.SportStringListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.customviews.LoadingView;
import sebastians.sportan.layouts.AreaContentLayout;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.GetImageTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;
import sebastians.sportan.tools.DateCalculations;

/**
 * Created by sebastian on 06/12/15.
 */
public class AreaDetailFragment extends Fragment implements AreaContentLayout.OnReleaseListener {
    MyCredentials myCredentials;
    ImageView area_img;
    TextView area_name_txt;
    TextView area_description_txt;
    EditText number_participants_txt;
    Area area;
    RecyclerView sport_list;
    Button been_here_btn;
    Button announce_activity_btn;
    LoadingView loadingView;
    String areaid;
    AreaContentLayout in_reveal;
    ImageView close_img;
    ImageView pin_img;
    Switch adm_swtch;
    ArrayList<String> favAreas = new ArrayList<>();

    public AreaDetailFragment() {

    }

    public void setAreaid(String id) {
        this.areaid = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_area_detail, container, false);
        final Activity mActivity = getActivity();
        myCredentials = new MyCredentials(mActivity);
        favAreas = myCredentials.getFavAreas();

        // To run the animation as soon as the view is layout in the view hierarchy we add this
        // listener and remove it
        // as soon as it runs to prevent multiple animations if the view changes bounds
// get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, view.getWidth() - cx);
        int dy = Math.max(cy, view.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);


        //created from intent
        Intent intent = mActivity.getIntent();
        final String areaid = this.areaid;



        loadingView = (LoadingView) view.findViewById(R.id.loading_view);
        area_img = (ImageView) view.findViewById(R.id.area_img);
        area_name_txt = (TextView) view.findViewById(R.id.area_name_txt);
        area_description_txt = (TextView) view.findViewById(R.id.area_description_txt);

        been_here_btn = (Button) view.findViewById(R.id.been_here_btn);
        been_here_btn.setVisibility(View.GONE);
        announce_activity_btn = (Button) view.findViewById(R.id.announce_activity_btn);
        announce_activity_btn.setVisibility(View.GONE);
        number_participants_txt = (EditText) view.findViewById(R.id.number_participants_txt);
        pin_img = (ImageView) view.findViewById(R.id.pin_img);
        close_img = (ImageView) view.findViewById(R.id.close_img);
        adm_swtch = (Switch) view.findViewById(R.id.adm_swtch);




        if(myCredentials.amIAdmin()){
            adm_swtch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        //load new fragment in this view!
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.addToBackStack(null);
                        AreaDetailAdminFragment areaDetailAdminFragment = new AreaDetailAdminFragment();
                        areaDetailAdminFragment.setAreaId(AreaDetailFragment.this.areaid);
                        ft.replace(R.id.area_detail_fragment_placeholder, areaDetailAdminFragment, "AreaDetailFragment");

                        ft.commit();
                        adm_swtch.setChecked(false);
                    }
                }
            });

        }else{
            adm_swtch.setVisibility(View.GONE);
        }


        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(AreaDetailFragment.this).commit();
            }
        });

        pin_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favAreas.contains(areaid)){
                    favAreas.remove(areaid);
                    myCredentials.setFavAreas(favAreas);
                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(0f);
                    final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                    pin_img.setColorFilter(colorFilter);
                }else{
                    favAreas.add(areaid);
                    myCredentials.setFavAreas(favAreas);
                    ColorMatrix colorMatrix = new ColorMatrix();
                    colorMatrix.setSaturation(1.0f);
                    final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                    pin_img.setColorFilter(colorFilter);
                }
            }
        });

        if(favAreas.contains(areaid)){
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1.0f);
            final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            pin_img.setColorFilter(colorFilter);
        }else{
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0f);
            final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            pin_img.setColorFilter(colorFilter);
        }


        in_reveal = (AreaContentLayout) view.findViewById(R.id.in_reveal);
        in_reveal.setOnReleaseListener(this);
        in_reveal.setAreaFav(favAreas.contains(areaid));


        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        sport_list = (RecyclerView) view.findViewById(R.id.sports);
        sport_list.setLayoutManager(layoutManager);

        final ArrayList<String> sportList = new ArrayList<>();
        final SportStringListAdapter sportListAdapter = new SportStringListAdapter(getActivity(), R.id.sports, sportList);
        sport_list.setAdapter(sportListAdapter);


        //get image for area;
        if (areaid != null && !areaid.equals("")) {
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
                                area = client.getAreaById(myCredentials.getToken(), areaid);
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

                            if (area.getSports() != null) {
                                ArrayList<String> asports = (ArrayList<String>) area.getSports();
                                sportList.clear();
                                sportList.addAll(asports);

                                sportListAdapter.notifyItemRangeChanged(0,sportList.size());
                                Log.i("AreaDetail", "sports " + sportList.size());


                            }

                        }
                    }
            );
            gatherInformationTask.execute("");

        }

        announce_activity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSportActivityFragment createSportActivityFragment = new CreateSportActivityFragment();
               // getFragmentManager().beginTransaction().replace(R.id.placeholder, createSportActivityFragment).commit();
            }
        });

        been_here_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.select_time)
                        .setItems(R.array.dates, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final long mDate;
                                //select current date
                                switch (which) {
                                    case 4:
                                        mDate = 0;
                                        break;
                                    case 3:
                                        mDate = DateCalculations.startOfDay(System.currentTimeMillis()) - 86400 * 1000 * 7;
                                        break;
                                    case 2:
                                        mDate = DateCalculations.startOfDay(System.currentTimeMillis()) - 86400 * 1000 * 2;
                                        break;
                                    case 1:
                                        mDate = DateCalculations.startOfDay(System.currentTimeMillis()) - 86400 * 1000;
                                        break;
                                    case 0:
                                    default:
                                        mDate = DateCalculations.startOfDay(System.currentTimeMillis());
                                }

                                //set area for user
                                final CustomAsyncTask submitActivityTask = new CustomAsyncTask(mActivity);
                                submitActivityTask.setTaskCallBacks(new TaskCallBacks() {
                                    @Override
                                    public String doInBackground() {
                                        TMultiplexedProtocol mp;
                                        try {
                                            mp = submitActivityTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                                            AreaSvc.Client client = new AreaSvc.Client(mp);
                                            client.wasHere(myCredentials.getToken(), areaid, mDate);
                                            submitActivityTask.closeTransport();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        return null;
                                    }

                                    @Override
                                    public void onPreExecute() {
                                        if (loadingView != null)
                                            loadingView.startAnimation();
                                    }

                                    @Override
                                    public void onPostExecute() {
                                        if (loadingView != null)
                                            loadingView.stopAnimation();
                                    }
                                });
                                submitActivityTask.execute();
                            }
                        }).create().show();

            }
        });


        return view;
    }


    /**
     * will close this fragment, when views are pulled out of window
     * @param releaseToClose
     */
    @Override
    public void onRelease(boolean releaseToClose) {
        if(releaseToClose)
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void closeToLeft(boolean left) {
        //pin up selected area!
        Log.i("AreaDetail", "closetoleft +" + left);

        if(!left) {
            if(!favAreas.contains(areaid)){
                favAreas.add(areaid);
                myCredentials.setFavAreas(favAreas);
            }

        }
    }
}
