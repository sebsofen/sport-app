package sebastians.sportan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sebastians.sportan.R;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Area;
import sebastians.sportan.tasks.GetAreaTask;
import sebastians.sportan.tasks.GetImageTask;
import sebastians.sportan.tasks.GetTaskFinishCallBack;

/**
 * Created by sebastian on 25/01/16.
 */
public class AreaFavAdapter extends RecyclerView.Adapter<AreaFavAdapter.AreaFavItemViewHolder> {
    Context context;
    ArrayList<String> favsList = new ArrayList<>();
    MyCredentials myCredentials;
    public AreaFavAdapter(Context context) {
        this.context = context;
        myCredentials = new MyCredentials(context);
        notifyDatasetChanged();
    }

    public void notifyDatasetChanged() {
        favsList.clear();
        favsList.addAll(myCredentials.getFavAreas());
    }



    @Override
    public AreaFavItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item, null);
        AreaFavItemViewHolder mh = new AreaFavItemViewHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final AreaFavItemViewHolder holder, int position) {
        final AreaFavItemViewHolder mh = holder;
        final String areaid = favsList.get(position);
        final GetAreaTask getAreaTask = new GetAreaTask(context, areaid, new GetTaskFinishCallBack<Area>() {
            @Override
            public void onFinished(Area area) {
                holder.area_name_txt.setText(area.getTitle());
                if (area.getImageid() != null && !area.getImageid().equals("")) {
                    GetImageTask imgTask = new GetImageTask(context, mh.area_img, area.getImageid());
                    imgTask.execute();
                }
            }
        });
        getAreaTask.execute();


    }

    @Override
    public int getItemCount() {
        return favsList.size();
    }

    public class AreaFavItemViewHolder extends RecyclerView.ViewHolder {
        protected ImageView area_img;
        protected TextView area_name_txt;
        public AreaFavItemViewHolder(View itemView) {
            super(itemView);
            area_img = (ImageView) itemView.findViewById(R.id.area_img);
            area_name_txt = (TextView) itemView.findViewById(R.id.area_name_txt);
        }
    }
}
