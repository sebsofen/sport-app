package sebastians.sportan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.GridView;

import java.util.ArrayList;

import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.SportListTask;

public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.list_refresh);
        final GridView sportListView = (GridView) findViewById(R.id.sport_list);
        Log.i("SportSelectActivity", "" + sportListView);
        final ArrayList<Sport> sportList = new ArrayList<>();
        final SportListAdapter sportListAdapter = new SportListAdapter(this,R.id.sport_select_layout,sportList);
        sportListView.setAdapter(sportListAdapter);
        sportListAdapter.notifyDataSetChanged();
        final Context mThis = this;

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                SportListTask sportListTask = new SportListTask(mThis);
                //get last known location

                sportListTask.setConnectedRefreshLayout(refreshLayout);
                sportListTask.setConnectedAdapter(sportListAdapter);
                sportListTask.connectArrayList(sportList);
                sportListTask.execute("");
            }
        });
    }

}
