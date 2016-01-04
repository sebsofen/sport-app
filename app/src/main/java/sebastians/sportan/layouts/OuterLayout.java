package sebastians.sportan.layouts;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import sebastians.sportan.R;

/**
 * Created by sebastian on 18/11/15.
 */
public class OuterLayout extends RelativeLayout {


    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int mDraggingState = 0;
    private ImageButton dragButton;
    private ViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;
    private View view;
    private final double[] DIVISIONS = {0.9, .6, .5};
    private int division;
    private int height = 0;


    public class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState) { // no change
                return;
            }

            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING) && state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.
                if (mDraggingBorder == 0) {
                    onStopDraggingToClosed();
                } else if (mDraggingBorder == mVerticalRange) {
                    mIsOpen = true;
                }
            }

            if (state == ViewDragHelper.STATE_DRAGGING) {
                onStartDragging();
            }

            mDraggingState = state;

        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            view = changedView;
            Log.i("OuterLayout", "onViewPositionChanged");
            mDraggingBorder = top;
        }

        public int getViewVerticalDragRange(View child) {
                return mVerticalRange;
            }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == R.id.sport_select_layout);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = mVerticalRange;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = mVerticalRange;
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }

            if (mDraggingBorder == rangeToCheck) {
                mIsOpen = true;
                return;
            }

            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (mDraggingBorder > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (mDraggingBorder < rangeToCheck / 2) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? mVerticalRange : 0;

            if(mDragHelper.settleCapturedViewAt(0, settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(OuterLayout.this);
            }
            }
        }

        public OuterLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            mIsOpen = true;
        }

        @Override
        protected void onFinishInflate() {
            dragButton  = (ImageButton) findViewById(R.id.dragg);
            mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
            mIsOpen = false;
            super.onFinishInflate();
        }

    /**
     * set current division for the dropdown to stop when scrolling
     * @param division
     */
    public void setDivision(int division) {
        if(division < 0 || division > DIVISIONS.length - 1)
            return;
        this.division = division;
        mVerticalRange = (int) (height * DIVISIONS[this.division]);
    }

    public void flyToDivision(int division) {
        Log.i("OuterLayout", "flyToDivision " + division);
        this.setDivision(division);

        MotionEvent motionEvent = MotionEvent.obtain(1000,1000,1,10f,(float)height, 1 );
        mDragHelper.processTouchEvent(motionEvent);


    }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            height = h;
            mVerticalRange = (int) (h * DIVISIONS[division]);
            super.onSizeChanged(w, h, oldw, oldh);
        }

        private void onStopDraggingToClosed() {
            // To be implemented
        }

        private void onStartDragging() {

        }

        private boolean isQueenTarget(MotionEvent event) {
            int[] queenLocation = new int[2];
            dragButton.getLocationOnScreen(queenLocation);
            int upperLimit = queenLocation[1] + dragButton.getMeasuredHeight();
            int lowerLimit = queenLocation[1];
            int y = (int) event.getRawY();
            return (y > lowerLimit && y < upperLimit);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (isQueenTarget(event) && mDragHelper.shouldInterceptTouchEvent(event)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.i("OuterLayout", "touchy");
            if (isQueenTarget(event) || isMoving()) {
                mDragHelper.processTouchEvent(event);
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override
        public void computeScroll() { // needed for automatic settling.
            if(!isMoving() && view != null) {
                view.setTop(mDraggingBorder);
            }
            if (mDragHelper.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        public boolean isMoving() {
            return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                    mDraggingState == ViewDragHelper.STATE_SETTLING);
        }

        public boolean isOpen() {
            return mIsOpen;
        }

}
