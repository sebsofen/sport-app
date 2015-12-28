package sebastians.sportan.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.R;
import sebastians.sportan.networking.Area;
import sebastians.sportan.tasks.GetAreaTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;

/**
 * Created by sebastian on 01/11/15.
 */
public class AreaListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> areaList;
    public AreaListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.areaList = (ArrayList<String>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View areaView = inflater.inflate(R.layout.remove_area_list_item, parent, false);
        final TextView title = (TextView) areaView.findViewById(R.id.area_title);

        String areaid = areaList.get(position);
        GetAreaTask getAreaTask = new GetAreaTask(context, areaid, new GetTaskFinishCallBack<Area>() {
            @Override
            public void onFinished(Area area) {
                title.setText(area.getTitle());
                Log.i("AREAADAPTER", "called");
            }
        });
        getAreaTask.execute();


        return areaView;
    }

}
