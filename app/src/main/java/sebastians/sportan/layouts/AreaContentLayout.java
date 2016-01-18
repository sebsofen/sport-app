package sebastians.sportan.layouts;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import sebastians.sportan.R;

/**
 * Created by sebastian on 18/01/16.
 */
public class AreaContentLayout extends LinearLayout {
    private ViewDragHelper mDragHelper;
    private RelativeLayout area_content_content;
    private RelativeLayout area_image_content;
    private int top_area_content_content = 0;
    private int top_area_image_content = 0;
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
        area_content_content = (RelativeLayout)findViewById(R.id.area_content_content);
        area_image_content = (RelativeLayout)findViewById(R.id.area_image_content);
        top_area_image_content = area_image_content.getTop();
        top_area_content_content = area_content_content.getTop();

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
        public int clampViewPositionVertical(View child, int top, int dy) {

            if(dy < 0)
                top += -dy;
            return top;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            int glob_top = AreaContentLayout.this.getTop();
            int glob_bottom = AreaContentLayout.this.getBottom();
            int glob_center = AreaContentLayout.this.getBottom() / 2;
            int new_image_position = area_image_content.getTop() - dy;
            area_image_content.setTop(new_image_position);

            //set alpha values
            area_image_content.setAlpha(1.5f - (float)(glob_center - new_image_position) / (float)glob_bottom);
            area_content_content.setAlpha(1.5f - (float)(glob_center - new_image_position) / (float)glob_bottom);


        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            boolean timeToClose = area_content_content.getTop() > .8 * AreaContentLayout.this.getHeight();
            if(AreaContentLayout.this.onReleaseListener != null)
                AreaContentLayout.this.onReleaseListener.onRelease(timeToClose);

            if(!timeToClose) {
                area_content_content.setTop(AreaContentLayout.this.getBottom() / 2);
                area_image_content.setTop(0);
                //set alpha values
                area_image_content.setAlpha(1.0f);
                area_content_content.setAlpha(1.0f);
            }

        }







    }

    public interface OnReleaseListener {
        void onRelease(boolean releaseToClose);
    }
}
