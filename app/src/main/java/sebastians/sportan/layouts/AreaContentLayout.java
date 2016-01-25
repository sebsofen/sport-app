package sebastians.sportan.layouts;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import sebastians.sportan.R;

/**
 * Created by sebastian on 18/01/16.
 */
public class AreaContentLayout extends RelativeLayout{
    private ViewDragHelper mDragHelper;
    private FrameLayout area_content_content;
    private OnReleaseListener onReleaseListener;
    private ImageView pin_img;
    private boolean area_fav =false;
    public AreaContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }


    public void setAreaFav(boolean fav){
        area_fav = fav;
    }


    public void setOnReleaseListener(OnReleaseListener onReleaseListener){
        this.onReleaseListener = onReleaseListener;
    }


    public AreaContentLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        area_content_content = (FrameLayout)findViewById(R.id.area_content_content);
        pin_img = (ImageView)findViewById(R.id.pin_img);

    }





    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }



    private class DragHelperCallback extends ViewDragHelper.Callback {


        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i("AreaContentLayout", "tryCaptureView");
            return child == area_content_content;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            return left;
        }



        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            int layoutwidth = AreaContentLayout.this.getWidth();
            int layoutHeight = AreaContentLayout.this.getHeight();
            Log.i("AreaContentLayout", "width:" + layoutwidth + ", " + (float)(left + (layoutwidth / 2)) / (float)layoutwidth);
            float alpha =(float)(left + (layoutwidth / 2)) / (float)layoutwidth + .5f;

            if (alpha > 1 ){
                alpha = 2.0f - alpha;
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(1.0f - alpha);
                final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                if(!area_fav)
                    pin_img.setColorFilter(colorFilter);

            }else {


            }
            changedView.setAlpha(alpha);
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int layoutwidth = AreaContentLayout.this.getWidth();

            int left = area_content_content.getLeft();
            Log.i("AreaContentLayout", "width:" + layoutwidth + ", " + (float)(left + (layoutwidth / 2)) / (float)layoutwidth);
            float alpha =(float)(left + (layoutwidth / 2)) / (float)layoutwidth + .5f;
            boolean leftdirection = false;
            if (alpha > 1){
                alpha = 2.0f - alpha;
                //reset alpha
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0f);
                final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                if(!area_fav)
                    pin_img.setColorFilter(colorFilter);
                leftdirection = true;
            }else {
                alpha = alpha;
                leftdirection = false;
            }
            boolean timeToClose = (alpha < .5);
            if(AreaContentLayout.this.onReleaseListener != null) {
                AreaContentLayout.this.onReleaseListener.onRelease(timeToClose);
                if(timeToClose) {
                    AreaContentLayout.this.onReleaseListener.closeToLeft(!leftdirection);
                }
            }

            if(!timeToClose) {
                releasedChild.setLeft(0);
                releasedChild.setRight(AreaContentLayout.this.getWidth());
                area_content_content.setAlpha(1.0f);
            }


        }







    }

    public interface OnReleaseListener {
        void onRelease(boolean releaseToClose);
        void closeToLeft(boolean left);
    }
}
