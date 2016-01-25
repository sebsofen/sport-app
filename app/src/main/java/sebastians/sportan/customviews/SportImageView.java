package sebastians.sportan.customviews;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by sebastian on 21/01/16.
 */
public class SportImageView extends ImageView {
    ColorMatrixColorFilter grayFilter;
    ColorMatrixColorFilter colorFilter;
    boolean selected = false;
    public SportImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //grayscale filter
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(.2f);
         grayFilter = new ColorMatrixColorFilter(grayMatrix);
        //color filter
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(1.2f);
         colorFilter = new ColorMatrixColorFilter(colorMatrix);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }


    public void setSelected(boolean selected){
        Log.i("SportImageView", "setSelected" + selected);
        //TODO IMPLEMENTATION
        if(selected) {
            this.setColorFilter(colorFilter);


        }else{
            this.setColorFilter(grayFilter);
        }
        this.selected = selected;
    }
}
