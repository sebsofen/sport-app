package sebastians.sportan.tasks;

import android.content.Context;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.tasks.caches.AreasCache;

/**
 * Created by sebastian on 18/12/15.
 */
public class GetAreaTask extends SuperAsyncTask {
    MyCredentials myCredentials;
    GetTaskFinishCallBack<Area> getTaskFinishCallBack;
    Context ctx;
    Area area;
    String areaid;

    public GetAreaTask(Context ctx, String areaid,GetTaskFinishCallBack<Area> taskFinishCallBack) {
        super(ctx);
        this.ctx = ctx;
        this.getTaskFinishCallBack = taskFinishCallBack;
        this.myCredentials = new MyCredentials(ctx);
        this.areaid = areaid;
    }

    @Override
    protected String doInBackground(String... strings) {
        if(AreasCache.get(areaid) != null){
            area = AreasCache.get(areaid);
            return null;
        }
        try {
            TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_AREA);
            AreaSvc.Client client = new AreaSvc.Client(mp);
            area = client.getAreaById(myCredentials.getToken(), this.areaid);
            AreasCache.add(areaid,area);
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
            getTaskFinishCallBack.onFinished(area);
    }
}
