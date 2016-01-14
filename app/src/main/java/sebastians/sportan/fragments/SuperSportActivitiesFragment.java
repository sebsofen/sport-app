package sebastians.sportan.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sebastians.sportan.R;

/**
 * Created by sebastian on 11/01/16.
 */
public class SuperSportActivitiesFragment extends Fragment {

    public static SuperSportActivitiesFragment newInstance() {
        return new SuperSportActivitiesFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_super_sportactivities, container, false);

        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(R.id.placeholder, MainAvailableSportActivitiesFragment.newInstance(), "MainSportActivities");
        ft.commit();

        return view;
    }

}
