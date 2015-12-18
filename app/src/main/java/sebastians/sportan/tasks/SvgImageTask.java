package sebastians.sportan.tasks;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.view.View;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sebastians.sportan.networking.Image;
import sebastians.sportan.networking.ImageSvc;
import sebastians.sportan.networking.InvalidOperation;
import sebastians.sportan.tasks.caches.ImagesCache;

/**
 * Created by sebastian on 21/11/15.
 */
public class SvgImageTask extends SuperAsyncTask{
    ImageView view;
    Image image;
    Context context;
    public SvgImageTask(Context ctx) {
        super(ctx);
        this.context = ctx;
    }

    public void setImageView(ImageView view){
        this.view = view;
    }


    @Override
    protected String doInBackground(String... strings) {
        String imageid = strings[0];

        //see, if cached files are availble


        if(ImagesCache.get(imageid) != null){
            image = ImagesCache.get(imageid);
            return null;
        }

        File file = new File(this.context.getCacheDir(),imageid);
        if(file.exists()){
            image = new Image();
            image.id = imageid;
            byte[] bytes = new byte[(int)file.length()];

            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                in.read(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            image.content = new String(bytes);
            return null;
        }

        try {
            TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_IMAGE);
            ImageSvc.Client client = new ImageSvc.Client(mp);
            image = client.getImageById(imageid);
            FileOutputStream stream = new FileOutputStream(file);
            ImagesCache.add(imageid,image);
            try {
                stream.write(image.content.getBytes());
            } finally {
                stream.close();
            }

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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(image.content != null) {

            try {
                SVG iconsvg = SVG.getFromString(image.content);
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                view.setAdjustViewBounds(true);
                view.setImageDrawable(new PictureDrawable(iconsvg.renderToPicture()));
            } catch (SVGParseException e) {
                e.printStackTrace();
            }
           /*  */
        }

    }


}
