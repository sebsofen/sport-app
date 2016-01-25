package sebastians.sportan.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sebastians.sportan.R;
import sebastians.sportan.adapters.AreaFavAdapter;

/**
 * Created by sebastian on 25/01/16.
 */
public class AreaFavoritesListFragment extends Fragment {
    private RecyclerView area_list;
    public static AreaFavoritesListFragment newInstance() {
        return new AreaFavoritesListFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_area_favorite, container, false);

        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        area_list = (RecyclerView) view.findViewById(R.id.area_list);
        area_list.setLayoutManager(layoutManager);


        final AreaFavAdapter areaFavAdapter = new AreaFavAdapter(getActivity());
        area_list.setAdapter(areaFavAdapter);


        return view;
    }

}