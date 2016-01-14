package sebastians.sportan.tasks;

import android.content.Context;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.ServiceConstants;
import sebastians.sportan.networking.SportActivity;
import sebastians.sportan.networking.SportActivitySvc;
import sebastians.sportan.tasks.caches.AreasCache;
import sebastians.sportan.tasks.caches.SportActivitiesCache;

/**
 * Created by sebastian on 18/12/15.
 */
public class GetSportActivityTask extends SuperAsyncTask {
    MyCredentials myCredentials;
    GetTaskFinishCallBack<SportActivity> getTaskFinishCallBack;
    Context ctx;
    SportActivity sportActivity;
    String sportactivityid;
    boolean skipBackground = false;

    public GetSportActivityTask(Context ctx, String sportactivityid, GetTaskFinishCallBack<SportActivity> taskFinishCallBack) {
        super(ctx);
        this.ctx = ctx;
        this.getTaskFinishCallBack = taskFinishCallBack;
        this.myCredentials = new MyCredentials(ctx);
        this.sportactivityid = sportactivityid;
    }


    @Override
    protected String doInBackground(String... strings) {
        if(skipBackground)
            return null;

        if(SportActivitiesCache.get(sportactivityid) != null){
            sportActivity = SportActivitiesCache.get(sportactivityid);
            return null;
        }
        try {
            TMultiplexedProtocol mp = openTransport(ServiceConstants.SERVICE_SPORTACTIVITY);
            SportActivitySvc.Client client = new SportActivitySvc.Client(mp);
            sportActivity = client.getActivity(myCredentials.getToken(), this.sportactivityid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        closeTransport();
        return null;
    }

    @Override
    protected void onPreExecute() {
        if(AreasCache.get(sportactivityid) != null){
            sportActivity = SportActivitiesCache.get(sportactivityid);
            skipBackground = true;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(sportActivity != null)
            SportActivitiesCache.add(sportActivity.getId(),sportActivity);

        if(getTaskFinishCallBack != null)
            getTaskFinishCallBack.onFinished(sportActivity);
    }
}
