package sebastians.sportan.tasks;

import android.content.Context;
import android.util.Log;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.networking.Area;
import sebastians.sportan.networking.AreaSvc;
import sebastians.sportan.networking.Coordinate;
import sebastians.sportan.networking.InvalidOperation;

/**
 * Created by sebastian on 09/11/15.
 */
public class AreaListTask extends SuperListTask<Area> {
    private Coordinate center;

    public void setCoordinate(Coordinate center){
        this.center = center;
    }

    public Coordinate getCoordinate(){
        return this.center;
    }

    public AreaListTask(Context ctx) {
        super(ctx);
        final AreaListTask thisTask = this;
        this.setGetList(new GetListInterface<Area>() {
            @Override
            public List<Area> getList(int limit) {
                ArrayList<Area> getListList = new ArrayList<>();
                if(thisTask.center == null)
                    return null;
                try {
                    TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_AREA);
                    AreaSvc.Client client = new AreaSvc.Client(mp);
                    getListList.clear();
                    getListList.addAll(client.getNearBy(thisTask.center, 10));
                    Log.i("AreaListTask", "returned elements " + getListList.size());
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
