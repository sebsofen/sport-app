package sebastians.sportan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SportListTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class AreaDetailActivity extends ActionBarActivity {
    public static final String EXTRA_AREA_ID = "areaid";
    TextView areaid_txt;
    Button submit_btn;
    GridView sport_list;
    EditText title_edit;
    EditText description_edit;
    public MyCredentials myCredentials;
    Area area;

    HashMap<String,Integer> selectedSports = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);
        areaid_txt = (TextView) findViewById(R.id.areaid_txt);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        sport_list = (GridView) findViewById(R.id.sport_list);

        title_edit = (EditText) findViewById(R.id.title_edit);
        description_edit= (EditText) findViewById(R.id.description_edit);

        myCredentials = new MyCredentials(this);
        //created from intent
        Intent intent = getIntent();
        final String areaid = intent.getStringExtra(AreaDetailActivity.EXTRA_AREA_ID);


        final ArrayList<Sport> sportList = new ArrayList<>();

        final SportListAdapter sportListAdapter = new SportListAdapter(this,R.id.sport_select_layout,sportList);
        sportListAdapter.setSelectedList(selectedSports);
        sport_list.setAdapter(sportListAdapter);
        final AreaDetailActivity mThis = this;

        SportListTask sportListTask = new SportListTask(mThis);
        //get last known location
        sportListTask.setConnectedAdapter(sportListAdapter);
        sportListTask.connectArrayList(sportList);
        sportListTask.execute("");

        sport_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sportid = sportList.get(position).getId();
                if (selectedSports.get(sportid) == null) {
                    selectedSports.put(sportid, 1);
                } else {
                    selectedSports.remove(sportid);
                }
                //TODO this can be reset to direct update in views (will improve performance)
                sportListAdapter.notifyDataSetChanged();


            }
        });


        areaid_txt.setText(areaid);
        final CustomAsyncTask gatherInformationTask = new CustomAsyncTask(this);
        gatherInformationTask.setTaskCallBacks(
                new TaskCallBacks() {

                    @Override
                    public String doInBackground() {
                        TMultiplexedProtocol mp = null;
                        try {
                            mp = gatherInformationTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                            AreaSvc.Client client = new AreaSvc.Client(mp);
                            area = client.getAreaById(areaid);


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
                        description_edit.setText(area.getDescription());
                        title_edit.setText(area.getTitle());
                        if (area.getSports() != null) {
                            ArrayList<String> asports = (ArrayList<String>) area.getSports();
                            for(int i = 0; i < asports.size(); i++){
                                selectedSports.put(asports.get(i),1);
                            }
                            sportListAdapter.notifyDataSetChanged();
                        }

                    }
                }
        );
        gatherInformationTask.execute("");

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send information to server!
                area.setTitle(title_edit.getText().toString());
                area.setDescription(description_edit.getText().toString());

                ArrayList<String> areaSportList = new ArrayList<String>();
                Iterator<String> it = selectedSports.keySet().iterator();
                while (it.hasNext()) {
                    areaSportList.add(it.next());
                }
                area.setSports(areaSportList);

                final CustomAsyncTask submitNewAreaTask = new CustomAsyncTask(mThis);
                submitNewAreaTask.setTaskCallBacks(
                        new TaskCallBacks() {
                            @Override
                            public String doInBackground() {
                                TMultiplexedProtocol mp = null;
                                try {
                                    mp = gatherInformationTask.openTransport(SuperAsyncTask.SERVICE_AREA);
                                    AreaSvc.Client client = new AreaSvc.Client(mp);
                                    client.updateArea(myCredentials.getToken(), area);

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
                                mThis.finish();
                            }
                        }
                );

                submitNewAreaTask.execute("");


            }
        });

    }

}
