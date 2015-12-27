package sebastians.sportan.tasks;

import android.content.Context;
import android.util.Log;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.networking.City;
import sebastians.sportan.networking.CitySvc;
import sebastians.sportan.networking.Coordinate;

/**
 * Created by sebastian on 07/11/15.
 */
public class CityListTask extends SuperListTask<City> {
    private Coordinate center;

    public void setCoordinate(Coordinate center){
        this.center = center;
    }

    public Coordinate getCoordinate(){
        return this.center;
    }

    public CityListTask(Context ctx) {
        super(ctx);
        final CityListTask thisTask = this;

        this.setGetList(new GetListInterface<City>() {
            @Override
            public List<City> getList(int limit) {
                ArrayList<City> getListList = new ArrayList<City>();

                if(thisTask.getCoordinate() == null) {
                   thisTask.setCoordinate(new Coordinate(1.0,1.0));
                }

                try {
                    TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_CITY);
                    CitySvc.Client client = new CitySvc.Client(mp);
                    Log.i("CityList", "lat: " + thisTask.getCoordinate().getLat());
                    Log.i("CityList", "lon: " + thisTask.getCoordinate().getLon());
                    getListList.addAll(client.getNearBy(thisTask.getCoordinate(), limit));
                } catch (Exception x) {
                    Log.i("CityList", x.getMessage());
                    x.printStackTrace();
                } finally {
                    transport.close();
                }
                return getListList;
            }
        });


    }
}
