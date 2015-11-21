package sebastians.sportan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.SvgImageTask;

/**
 * Created by sebastian on 21/11/15.
 */
public class SportListAdapter extends ArrayAdapter<Sport> {
    Context context;
    ArrayList<Sport> sportList;
    public SportListAdapter(Context context, int resource, List<Sport> objects) {
        super(context, resource, objects);
        this.context = context;
        this.sportList = (ArrayList<Sport>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("SportListAdapter", "position:"+ position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View elementView = inflater.inflate(R.layout.sport_select_item, parent, false);

        SvgImageTask imageTask = new SvgImageTask(context);
        ImageView iconView = (ImageView) elementView.findViewById(R.id.sport_icon);
        TextView title = (TextView) elementView.findViewById(R.id.name);

        Sport sport = sportList.get(position);
        imageTask.setImageView(iconView);
        imageTask.execute(sport.getIconid());
        title.setText(sport.getName());

        Log.i("AREAADAPTER", "called");

        return elementView;
    }
}
