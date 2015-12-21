package sebastians.sportan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.R;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.GetSportTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;
import sebastians.sportan.tasks.SvgImageTask;

/**
 * Created by sebastian on 20/12/15.
 */
public class SportStringListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> sportList;


    public SportStringListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.sportList = (ArrayList<String>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View elementView = inflater.inflate(R.layout.sport_select_item, parent, false);

        final SvgImageTask imageTask = new SvgImageTask(context);
        final ImageView iconView = (ImageView) elementView.findViewById(R.id.sport_icon);
        final TextView title = (TextView) elementView.findViewById(R.id.name);

        String sportid = sportList.get(position);
        imageTask.setImageView(iconView);

        GetSportTask getSportTask = new GetSportTask(context, sportid, new GetTaskFinishCallBack<Sport>() {
            @Override
            public void onFinished(Sport sport) {
                imageTask.execute(sport.getIconid());
                title.setText(sport.getName().toUpperCase());
            }
        });
        getSportTask.execute();
        return elementView;
    }

}
