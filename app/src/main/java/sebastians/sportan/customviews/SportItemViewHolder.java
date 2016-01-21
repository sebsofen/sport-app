package sebastians.sportan.customviews;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import sebastians.sportan.R;

/**
 * Created by sebastian on 21/01/16.
 */
public class SportItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView icon_view;
    //TODO add imageview and stuff here!
    public SportItemViewHolder(View itemView) {
        super(itemView);
        icon_view = (ImageView) itemView.findViewById(R.id.sport_icon);
    }
}
