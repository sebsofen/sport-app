package sebastians.sportan.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import sebastians.sportan.R;
import sebastians.sportan.adapters.SportListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.networking.SportActivity;
import sebastians.sportan.tasks.SportListTask;

/**
 * Created by sebastian on 09/01/16.
 */
public class CreateSportActivityFragment extends Fragment {
    private CreateSportActivityListener mListener;
    private TextView date_txt;
    private MyCredentials myCredentials;
    private EditText description_edit;
    private GridView sport_list;
    public CreateSportActivityFragment() {
        sportActivity = new SportActivity();
    }

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    SportActivity sportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("SelectCityFragment", "create fragment view yo");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_announce_sportactivity, container, false);
        myCredentials = new MyCredentials(getActivity());
        date_txt = (TextView) view.findViewById(R.id.date_txt);
        description_edit = (EditText) view.findViewById(R.id.description_edit);
        sport_list = (GridView) view.findViewById(R.id.sportselect);

        final ArrayList<Sport> sportList = new ArrayList<>();

        final SportListAdapter sportListAdapter = new SportListAdapter(getActivity(),R.id.sport_select_layout,sportList);
        sport_list.setAdapter(sportListAdapter);
        sportListAdapter.setSingleSelection(true);
        SportListTask sportListTask = new SportListTask(getActivity());
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute("");


        date_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateSportActivityFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        CreateSportActivityFragment.this.year = year;
                        CreateSportActivityFragment.this.month = monthOfYear;
                        CreateSportActivityFragment.this.day = dayOfMonth;

                        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateSportActivityFragment.this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                CreateSportActivityFragment.this.hour = hour;
                                CreateSportActivityFragment.this.minute = minute;

                                date_txt.setText(
                                        CreateSportActivityFragment.this.day + "." +
                                        (CreateSportActivityFragment.this.month + 1) + "." +
                                        CreateSportActivityFragment.this.year + " " +
                                        CreateSportActivityFragment.this.hour + ":" +
                                        CreateSportActivityFragment.this.minute + ""
                                );
                                calendar.set(Calendar.MINUTE,CreateSportActivityFragment.this.minute);
                                calendar.set(Calendar.HOUR_OF_DAY,CreateSportActivityFragment.this.hour);
                                calendar.set(Calendar.YEAR,CreateSportActivityFragment.this.year);
                                calendar.set(Calendar.MONTH,CreateSportActivityFragment.this.month);
                                calendar.set(Calendar.DAY_OF_MONTH,CreateSportActivityFragment.this.day);

                                sportActivity.setDate(calendar.getTimeInMillis());

                            }

                        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
                        timePickerDialog.show();

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });



        Button announce_btn = (Button)view.findViewById(R.id.announce_btn);
        announce_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CreateSport", "announcebutton");
                //submit etc
                //TODO
                sportActivity.setDescription(description_edit.getText().toString());
                sportActivity.setHostid(myCredentials.Me.getIdentifier());
                sportActivity.setCityid(myCredentials.Me.getProfile().getCity_id());
                sportActivity.setSport(sportListAdapter.getSelectedSport());
                if(mListener != null) {
                    Log.i("CreateSport", "calling listener");
                    mListener.activityCreated(sportActivity);
                }else{

                }
                getActivity().getFragmentManager().beginTransaction().remove(CreateSportActivityFragment.this).commit();
            }
        });


        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CreateSportActivityListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CreateSportActivityListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onCreateSportActivity(CreateSportActivityListener createSportActivityListener){
        this.mListener = createSportActivityListener;
    }


    public interface CreateSportActivityListener {
        void activityCreated(SportActivity sportActivity);
    }
}
