package sebastians.sportan.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import sebastians.sportan.R;

/**
 * Loading View (hopping ball)
 *
 */
public class LoadingView extends View {
    Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float ballRadius = 10;
    float ballXPos = 0.0f;
    boolean ballLeftDirection = false;
    float leftMargin = 25;
    float rightMargin = 25;
    float increments = 10;
    boolean animate = true;
    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        ballPaint.setColor(getResources().getColor(R.color.colorFourth));
        ballPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(animate) {
            super.onDraw(canvas);
            float h = canvas.getHeight();
            float w = canvas.getWidth();
            increments = 10 * (h / 500);
            float ballYPos = 0.0f;
            if (ballLeftDirection) {
                //ball is going in left direction
                if (ballXPos < leftMargin) {
                    ballLeftDirection = false;
                }
                ballXPos -= increments;
            } else {
                //ball is going in right direction
                if (ballXPos > w - rightMargin) {
                    ballLeftDirection = true;
                }
                ballXPos += increments;
                //draw ball
            }
            ballYPos = h - (getYBallPosition(ballXPos / w) * h);
            canvas.drawCircle(ballXPos, ballYPos, ballRadius, ballPaint);
            this.redraw();
        }
    }

    /**
     * will compute ball position
     * IMPORTANT:
     * @param xposition has to be value in interval [0,1]
     * @return calculated y position
     */
    private float getYBallPosition(float xposition) {
        float turnpoint = (float) ((( Math.PI) / 3.0 ) - Math.PI / 6.0);

        if(xposition < 0 || xposition > 1)
            return Float.MIN_VALUE;

        if(xposition > turnpoint){
            return (float)(Math.sin(xposition * 3.0 - ( Math.PI / 2.0 )));
        }else{
            return (float) Math.cos(xposition * 3.0);
        }

    }

    public void redraw() {


            invalidate();
            requestLayout();

    }


    public void stopAnimation(){
        animate = false;
    }

    public void startAnimation() {
        animate = true;
    }

}
