
package sebastians.sportan.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.R;
import sebastians.sportan.networking.Coordinate;

/**
 * Created by sebastian on 07/11/15.
 */
public class GeoContourView extends View {

    ArrayList<Coordinate> contourList = new ArrayList<>();
    ArrayList<Pair<Coordinate, Integer>> pois = new ArrayList<>();
    double[] bboxTL = new double[2];
    double[] bboxBR = new double[2];


    public GeoContourView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init(){
        //initialize paint and stuff
    }

    public void addPOI(Coordinate coord, int colorid){
        Pair<Coordinate, Integer> poi = new Pair<>(coord,colorid);
        pois.add(poi);
    }

    public void setContourList(List<Coordinate> coords){
        contourList = (ArrayList)coords;
        double[] bboxTL = {Double.MAX_VALUE, Double.MAX_VALUE};
        double[] bboxBR = {Double.MIN_VALUE, Double.MIN_VALUE};
        for(int i = 0; i < coords.size(); i++){
            Coordinate curCoord = coords.get(i);
            if(Math.toRadians(curCoord.getLat()) < bboxTL[0]){
                bboxTL[0] = Math.toRadians(curCoord.getLat());
            }
            if(Math.toRadians(curCoord.getLon()) < bboxTL[1]){
                bboxTL[1] = Math.toRadians(curCoord.getLon());
            }

            if(Math.toRadians(curCoord.getLat()) > bboxBR[0]){
                bboxBR[0] = Math.toRadians(curCoord.getLat());
            }
            if(Math.toRadians(curCoord.getLon()) > bboxBR[1]){
                bboxBR[1] = Math.toRadians(curCoord.getLon());
            }
        }
        this.bboxBR = bboxBR;
        this.bboxTL = bboxTL;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i("ratio", "ratio" + ((this.bboxBR[0] - this.bboxTL[0]) / (this.bboxBR[1] - this.bboxTL[1])));

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        double ratio = 1;//((this.bboxBR[0] - this.bboxTL[0]) / (this.bboxBR[1] - this.bboxTL[1]));
        Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentPaint.setStyle(Paint.Style.FILL);
        currentPaint.setColor(getResources().getColor(R.color.background_floating_material_dark));

        for(int i = 0; i < contourList.size(); i += 1){
            double[] curcoord = getPxForLatLon(contourList.get(i % contourList.size()));
            double[] curcoord2 = getPxForLatLon(contourList.get((i+1) % contourList.size()));


            int y = (int)((double) width * curcoord[0]);
            int x = (int)((double) height * curcoord[1]  * ratio);
            int y2 = (int)((double) width * curcoord2[0]);
            int x2 = (int)((double) height * curcoord2[1] * ratio);
            canvas.drawLine(x, y, x2, y2, currentPaint);

        }

        for(int i = 0; i < pois.size(); i++){
            Log.i("location", pois.get(i).first.getLat() + " " + (pois.get(i).first.getLon()));
            double[] curcoord = getPxForLatLon(pois.get(i).first);
            currentPaint.setColor(getResources().getColor(pois.get(i).second));
            int y = (int)((double) width * curcoord[0]);
            int x = (int)((double) height * curcoord[1]  * ratio);
            canvas.drawCircle(x,y,5.0f,currentPaint);
        }


    }

    public double[] getPxForLatLon(Coordinate coord){
        double[] curcoord = {Math.toRadians(coord.getLat()),Math.toRadians(coord.getLon())};
        curcoord[0] =  (this.bboxBR[0] - curcoord[0]) / (this.bboxBR[0] - this.bboxTL[0]);
        curcoord[1] = 1 - ((this.bboxBR[1] - curcoord[1]) / (this.bboxBR[1] - this.bboxTL[1]) );

        return curcoord;
    }
}


