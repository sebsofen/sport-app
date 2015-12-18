package sebastians.sportan.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.Image;
import sebastians.sportan.networking.ImageSvc;
import sebastians.sportan.networking.InvalidOperation;
import sebastians.sportan.tasks.caches.ImagesCache;

/**
 * //TODO TRY TO CACHE IMAGE IN FILE SYSTEM! 
 */
public class GetImageTask extends SuperAsyncTask {
    Context ctx;
    MyCredentials myCredentials;
    ImageView resultView;
    String imageid;
    Image image;
    Boolean thumbnail;

    public GetImageTask(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        myCredentials = new MyCredentials(ctx);
    }

    public GetImageTask(Context ctx, ImageView resultView, String imageid){
       this(ctx,resultView,imageid,false);

    }

    public GetImageTask(Context ctx, ImageView resultView, String imageid, boolean thumbnail) {
        super(ctx);
        this.ctx = ctx;
        this.resultView = resultView;
        this.imageid = imageid;
        this.thumbnail = thumbnail;
    }



    @Override
    protected String doInBackground(String... strings) {
        super.doInBackground(strings);
        //see, if cached files are available

        if(this.thumbnail) {
            //TODO Try to read thumbnail first, and then load big image!
            //TO BE IMPLEMENTED
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(ImagesCache.get(imageid) != null){
            image = ImagesCache.get(imageid);
            return null;
        }

        try {
            TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_IMAGE);
            ImageSvc.Client client = new ImageSvc.Client(mp);
            image = client.getImageById(imageid);
            ImagesCache.add(imageid,image);
        } catch (InvalidOperation x) {
            x.printStackTrace();
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            transport.close();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("disImage", "TRYING TO DISPLAY IMAGE");
        if(image != null && image.getBcontent() != null) {
            Log.i("disImage", "WORKING");
            Bitmap bMap = BitmapFactory.decodeByteArray(image.getBcontent(), 0, image.getBcontent().length);
            resultView.setImageBitmap(bMap);
        }
    }
}
