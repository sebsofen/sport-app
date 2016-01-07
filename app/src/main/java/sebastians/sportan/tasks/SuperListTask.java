package sebastians.sportan.tasks;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

/**
 * Created by sebastian on 07/11/15.
 */
public class SuperListTask<T> extends SuperAsyncTask {
    GetListInterface<T> getListInterface;

    private ArrayAdapter connectedAdapter;
    private SwipeRefreshLayout connectedRefreshLayout;
    private ArrayList<T> connectedList = new ArrayList<>();

    public void connectArrayList(ArrayList<T> list){
        connectedList = list;
    }


    public void setConnectedRefreshLayout(SwipeRefreshLayout layout){
        this.connectedRefreshLayout = layout;
    }
    public void setConnectedAdapter(ArrayAdapter adapter){
        connectedAdapter = adapter;
    }


    public SuperListTask(Context ctx) {
        super(ctx);
    }

    public void setGetList(GetListInterface<T> getList){
        getListInterface = getList;
    }

    @Override
    protected String doInBackground(String... strings) {
        connectedList.clear();
        ArrayList<T> list = (ArrayList) getListInterface.getList(10);
        if(list != null)
            connectedList.addAll(list);

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(this.connectedRefreshLayout != null)
            this.connectedRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(connectedAdapter != null) {
            Log.i("logg", "datasetchange");
            Log.i("logg", "listsize " + connectedList.size());
            connectedAdapter.notifyDataSetChanged();
        }else{
            Log.i("logg", "was ist hier lows");
        }

        if(this.connectedRefreshLayout != null)
            this.connectedRefreshLayout.setRefreshing(false);

    }



}

