package sebastians.sportan.tasks;

import android.content.Context;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Coordinate;
import sebastians.sportan.networking.InvalidOperation;

/**
 * Created by sebastian on 09/11/15.
 */
public class AreaListTask extends SuperListTask<String> {
    private Coordinate center;
    MyCredentials myCredentials;

    public void setCoordinate(Coordinate center){
        this.center = center;
    }

    public Coordinate getCoordinate(){
        return this.center;
    }

    public AreaListTask(Context ctx) {
        super(ctx);
        if(myCredentials.Me == null)
            myCredentials = new MyCredentials(ctx);

        final AreaListTask thisTask = this;
        this.setGetList(new GetListInterface<String>() {
            @Override
            public List<String> getList(int limit) {
                ArrayList<String> getListList = new ArrayList<>();
                if(thisTask.center == null)
                    return null;
                try {
                    TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_AREA);
                    AreaSvc.Client client = new AreaSvc.Client(mp);
                    getListList.clear();
                    getListList.addAll(client.getNearBy(myCredentials.getToken(),thisTask.center, 100));
                } catch (InvalidOperation x) {
                    x.printStackTrace();
                } catch (Exception x) {
                    x.printStackTrace();
                } finally {
                    transport.close();
                }
                return getListList;
            }
        });
    }

}
