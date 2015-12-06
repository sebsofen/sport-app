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

/**
 * Created by sebastian on 01/11/15.
 */
public class AreaListAdapter extends ArrayAdapter<Area> {
    Context context;
    ArrayList<Area> areaList;
    public AreaListAdapter(Context context, int resource, List<Area> objects) {
        super(context, resource, objects);
        this.context = context;
        this.areaList = (ArrayList<Area>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View areaView = inflater.inflate(R.layout.area_list_item, parent, false);
        TextView title = (TextView) areaView.findViewById(R.id.area_title);

        Area area = areaList.get(position);
        title.setText(area.getTitle());
        Log.i("AREAADAPTER", "called");

        return areaView;
    }

}
