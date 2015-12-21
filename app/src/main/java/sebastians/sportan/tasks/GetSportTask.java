package sebastians.sportan.tasks;

import android.content.Context;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.networking.SportSvc;
import sebastians.sportan.tasks.caches.SportsCache;

/**
 * Created by sebastian on 20/12/15.
 */
public class GetSportTask extends SuperAsyncTask{
    Sport sport;
    String sportid;
    Context ctx;
    MyCredentials myCredentials;
    GetTaskFinishCallBack<Sport> getTaskFinishCallBack;
    public GetSportTask(Context ctx) {
        super(ctx);
    }
    public GetSportTask(Context ctx, String sportid, GetTaskFinishCallBack<Sport> getTaskFinishCallBack){
        super(ctx);
        this.getTaskFinishCallBack = getTaskFinishCallBack;
        this.sportid = sportid;
        this.ctx = ctx;
        this.myCredentials = new MyCredentials(ctx);
    }


    @Override
    protected String doInBackground(String... strings) {
        if(SportsCache.get(sportid) != null){
            sport = SportsCache.get(sportid);
            return null;
        }
        try {
            TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_SPORT);
            SportSvc.Client client = new SportSvc.Client(mp);
            sport = client.getSportById(myCredentials.getToken(), this.sportid);
            SportsCache.add(sport);
        } catch (Exception e) {
            e.printStackTrace();
        }

        closeTransport();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(getTaskFinishCallBack != null)
            getTaskFinishCallBack.onFinished(sport);
    }
}
