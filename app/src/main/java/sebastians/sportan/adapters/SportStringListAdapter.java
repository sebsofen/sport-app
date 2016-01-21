package sebastians.sportan.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.R;
import sebastians.sportan.customviews.SportItemViewHolder;
import sebastians.sportan.networking.Image;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.GetSportTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;
import sebastians.sportan.tasks.ImageReady;
import sebastians.sportan.tasks.SvgImageTask;

/**
 * Created by sebastian on 20/12/15.
 */
//public class SportStringListAdapter extends ArrayAdapter<String> {
public class SportStringListAdapter extends RecyclerView.Adapter<SportItemViewHolder>  {
    Context context;
    ArrayList<String> sportList;


    public SportStringListAdapter(Context context, int resource, List<String> objects) {

        this.context = context;
        this.sportList = (ArrayList<String>) objects;
    }


    @Override
    public SportItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("SportStringListAdapter", "oncreateviewholder");

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_item_small, null);
        SportItemViewHolder mh = new SportItemViewHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(SportItemViewHolder holder, int position) {
        final SportItemViewHolder mh = holder;
        Log.i("SportStringListAdapter", "getSportview");
        final SvgImageTask imageTask = new SvgImageTask(context);
        String sportid = sportList.get(position);
        imageTask.setImageView(holder.icon_view);

        GetSportTask getSportTask = new GetSportTask(context, sportid, new GetTaskFinishCallBack<Sport>() {
            @Override
            public void onFinished(Sport sport) {
                imageTask.onImageReady(new ImageReady() {
                    @Override
                    public void ready(Image image) {
                        try {
                            SVG iconsvg = SVG.getFromString(image.content);
                            final Bitmap iconBitmap = Bitmap.createBitmap(63,63, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(iconBitmap);
                            iconsvg.renderToCanvas(canvas);
                            mh.icon_view.setImageBitmap(iconBitmap);
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                imageTask.execute(sport.getIconid());

            }
        });
        getSportTask.execute();

    }

    @Override
    public int getItemCount() {
        return sportList.size();
    }
}
