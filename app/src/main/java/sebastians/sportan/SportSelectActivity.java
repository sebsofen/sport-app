package sebastians.sportan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import sebastians.sportan.layouts.OuterLayout;

public class SportSelectActivity extends Activity implements View.OnClickListener {
    private Button mQueen;
    private Button mHidden;
    private OuterLayout mOuterLayout;
    private RelativeLayout mMainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_select);
        mOuterLayout = (OuterLayout) findViewById(R.id.outer_layout);
        mMainLayout = (RelativeLayout) findViewById(R.id.sport_select_layout);



        Button mButten = (Button)mMainLayout.findViewById(R.id.button2);

        mButten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation anim= new TranslateAnimation(0, 0, 0, 200);
                anim.setDuration(200);
                anim.setFillAfter(false);
                mMainLayout.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mMainLayout.postInvalidateOnAnimation();
                        mMainLayout.clearAnimation();
                        mMainLayout.setTop(0);
                        mMainLayout.setTop(200);
                        mMainLayout.setBottom(mMainLayout.getBottom() + 200);
                        mMainLayout.setBottom(mMainLayout.getBottom() + 200);
                        //
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });
        mMainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mOuterLayout.isMoving()) {
                    v.setTop(oldTop);
                    v.setBottom(oldBottom);
                    v.setLeft(oldLeft);
                    v.setRight(oldRight);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        Toast t = Toast.makeText(this, b.getText() + " clicked", Toast.LENGTH_SHORT);
        t.show();
    }

}
