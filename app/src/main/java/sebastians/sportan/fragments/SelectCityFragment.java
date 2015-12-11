package sebastians.sportan.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import sebastians.sportan.R;
import sebastians.sportan.adapters.CityListAdapter;
import sebastians.sportan.networking.City;
import sebastians.sportan.networking.Coordinate;
import sebastians.sportan.tasks.CityListTask;



public class SelectCityFragment extends Fragment {




    private SelectedCityListener mListener;



    public SelectCityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("SelectCityFragment", "create fragment view yo");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_select_city, container, false);
        final SelectCityFragment mThis = this;



        //connect listview to adapter and task!
        final ListView cityListView = (ListView) view.findViewById(R.id.city_list);
        final ArrayList<City> cityArrayList = new ArrayList<>();
        final CityListAdapter cityListAdapter = new CityListAdapter(this.getActivity(),R.id.area_list,cityArrayList);
        cityListView.setAdapter(cityListAdapter);

        final SelectCityFragment myFragement = this;


        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.city_list_refresh);
        CityListTask cityListTask= new CityListTask(myFragement.getActivity());
        //get last known location
        LocationManager locationManager = (LocationManager) myFragement.getActivity().getSystemService(myFragement.getActivity().LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {

            Coordinate coord = new Coordinate(location.getLatitude(), location.getLongitude());
            cityListTask.setCoordinate(coord);
            cityListAdapter.setUserLocation(coord);
        }
        cityListTask.setConnectedRefreshLayout(swipeRefreshLayout);
        cityListTask.setConnectedAdapter(cityListAdapter);
        cityListTask.connectArrayList(cityArrayList);
        cityListTask.execute("");



        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener != null){
                    mListener.citySelected(cityArrayList.get(position));
                }
                getActivity().getFragmentManager().beginTransaction().remove(mThis).commit();
            }
        });





        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SelectedCityListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SelectedCityListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SelectedCityListener {
        // TODO: Update argument type and name
        void citySelected(City city);
    }

}
