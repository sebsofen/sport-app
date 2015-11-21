package sebastians.sportan.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by sebastian on 21/11/15.
 */
public class RoundMarker  {

    public static Bitmap RoundMarker(int r, int g, int b) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        int size = 40;
        Bitmap bmp = Bitmap.createBitmap(size, size, conf);

        Canvas canvas1 = new Canvas(bmp);
        Paint color = new Paint();
        color.setARGB(200, r, g,b);
        canvas1.drawCircle(size/2,size/2,size/2,color);

        return bmp;
    }
}
