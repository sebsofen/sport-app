package sebastians.sportan.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sebastians.sportan.R;

/**
 * Created by sebastian on 11/12/15.
 */
public class PageFragment extends Fragment {


    public static PageFragment newInstance(int page) {
       
        PageFragment fragment = new PageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        return view;
    }
}
