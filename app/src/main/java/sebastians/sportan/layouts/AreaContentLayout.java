package sebastians.sportan.layouts;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import sebastians.sportan.R;

/**
 * Created by sebastian on 18/01/16.
 */
public class AreaContentLayout extends RelativeLayout{
    private ViewDragHelper mDragHelper;
    private FrameLayout area_content_content;
    private OnReleaseListener onReleaseListener;
    public AreaContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }


    public void setOnReleaseListener(OnReleaseListener onReleaseListener){
        this.onReleaseListener = onReleaseListener;
    }


    public AreaContentLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        area_content_content = (FrameLayout)findViewById(R.id.area_content_content);


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
            return left + dx;
        }



        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            /*
            int glob_top = AreaContentLayout.this.getTop();
            int glob_bottom = AreaContentLayout.this.getBottom();
            int glob_center = AreaContentLayout.this.getBottom() / 2;
            int new_image_position = area_image_content.getTop() - dy;
            area_image_content.setTop(new_image_position);

            //set alpha values
            area_image_content.setAlpha(1.5f - (float)(glob_center - new_image_position) / (float)glob_bottom);
            area_content_content.setAlpha(1.5f - (float)(glob_center - new_image_position) / (float)glob_bottom);
            */

            int layoutwidth = AreaContentLayout.this.getWidth();


            Log.i("AreaContentLayout", "width:" + layoutwidth + ", " + (float)(left + (layoutwidth / 2)) / (float)layoutwidth);
            float alpha =(float)(left + (layoutwidth / 2)) / (float)layoutwidth + .5f;

            if (alpha > 1){
                alpha = 2.0f - alpha;
            }else {
                alpha = alpha;
            }


            changedView.setAlpha(alpha);
            Log.i("AreaContentLayout", "alpha:" + alpha);

        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int layoutwidth = AreaContentLayout.this.getWidth();

            int left = area_content_content.getLeft();
            Log.i("AreaContentLayout", "width:" + layoutwidth + ", " + (float)(left + (layoutwidth / 2)) / (float)layoutwidth);
            float alpha =(float)(left + (layoutwidth / 2)) / (float)layoutwidth + .5f;

            if (alpha > 1){
                alpha = 2.0f - alpha;
            }else {
                alpha = alpha;
            }
            boolean timeToClose = (alpha < .5);
            if(AreaContentLayout.this.onReleaseListener != null)
                AreaContentLayout.this.onReleaseListener.onRelease(timeToClose);

            if(!timeToClose) {
                area_content_content.setLeft(0);
                area_content_content.setAlpha(1.0f);
            }


        }







    }

    public interface OnReleaseListener {
        void onRelease(boolean releaseToClose);
    }
}
