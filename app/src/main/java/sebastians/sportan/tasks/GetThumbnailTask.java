package sebastians.sportan.tasks;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by sebastian on 28/11/15.
 */
public class GetThumbnailTask extends SuperAsyncTask {
    public GetThumbnailTask(Context ctx) {
        super(ctx);
    }

    public GetThumbnailTask(Context ctx, ImageView resultView, String imageid, boolean thumbnail) {
        super(ctx);
    }


}
