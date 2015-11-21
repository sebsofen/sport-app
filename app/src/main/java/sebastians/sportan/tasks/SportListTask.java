package sebastians.sportan.tasks;

import android.content.Context;
import android.util.Log;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.networking.InvalidOperation;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.networking.SportSvc;

/**
 * Created by sebastian on 20/11/15.
 */
public class SportListTask extends SuperListTask<Sport> {

    public SportListTask(Context ctx) {
        super(ctx);
    final SportListTask thisTask = this;
    this.setGetList(new GetListInterface<Sport>() {
        @Override
        public List<Sport> getList(int limit) {
            ArrayList<Sport> getListList = new ArrayList<>();
            try {
                TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_SPORT);
                SportSvc.Client client = new SportSvc.Client(mp);
                getListList.clear();
                ArrayList<Sport> sports = (ArrayList<Sport>)client.getAllSports("");
                for(int i = 0; i < sports.size(); i++){
                    Log.i("SportListTask", "sport: " + sports.get(i).getName());
                }
                getListList.addAll(sports);

            } catch (InvalidOperation x) {
                x.printStackTrace();
            } catch (Exception x) {
                x.printStackTrace();
            } finally {
                transport.close();
            }
            Log.i("AreaListTask", "returned elements " + getListList.size());
            return getListList;
        }
    });

    }

}
