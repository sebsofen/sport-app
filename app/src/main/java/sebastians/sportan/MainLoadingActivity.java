package sebastians.sportan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.List;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.app.MyCredentialsFinishedCallBack;
import sebastians.sportan.customviews.LoadingView;
import sebastians.sportan.fragments.SelectCityFragment;
import sebastians.sportan.networking.City;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class MainLoadingActivity extends ActionBarActivity implements MyCredentialsFinishedCallBack, SelectCityFragment.SelectedCityListener {
    LoadingView loadingView;
    MyCredentials myCredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loading);
        loadingView = (LoadingView) findViewById(R.id.loading_view);

        myCredentials = new MyCredentials(this,this);
    }

    @Override
    public void onFinish() {
        Log.i("fin", "FINISHED");
        if(loadingView != null)
            loadingView.stopAnimation();

        if(MyCredentials.Me.getProfile().getCity_id() != null){
            Uri data = getIntent().getData();
            if(data != null) {
                List<String> params = data.getPathSegments();
                Intent intent = new Intent(this, FriendsActivity.class);
                intent.putExtra("USER", params.get(params.size() - 1));
                startActivity(intent);
            }else{
                    startMainActivity();
                }
        }else{
            loadingView.startAnimation();
            Log.i("Entry", "no city for user exists");
            getFragmentManager().beginTransaction().replace(R.id.select_city_container, new SelectCityFragment()).commit();
        }


    }

    @Override
    public void citySelected(City city) {
        // update user profile!
        MyCredentials.Me.getProfile().setCity_id(city.getId());
        final CustomAsyncTask asyncTask = new CustomAsyncTask(this);
        asyncTask.setTaskCallBacks(new TaskCallBacks() {
            @Override
            public String doInBackground() {
                TMultiplexedProtocol mp = null;
                try {
                    mp = asyncTask.openTransport(SuperAsyncTask.SERVICE_USER);
                    UserSvc.Client client = new UserSvc.Client(mp);
                    client.setProfile(myCredentials.getToken(), MyCredentials.Me.getProfile());
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
                loadingView.stopAnimation();
                startMainActivity();
            }
        });
            asyncTask.execute();
    }

    /**
     * Main activity will be started, after all configuration stuff is done!
     */
    private void startMainActivity(){
        //TODO CHANGE TO CORRECT MAIN ACTIVITY
        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);

    }
}
