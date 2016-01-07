package sebastians.sportan.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sebastians.sportan.R;
import sebastians.sportan.layouts.OuterLayout;
import sebastians.sportan.networking.Image;
import sebastians.sportan.networking.Sport;
import sebastians.sportan.tasks.ImageReady;
import sebastians.sportan.tasks.SvgImageTask;

/**
 * Created by sebastian on 21/11/15.
 */
public class SportListAdapter extends ArrayAdapter<Sport> {
    Context context;
    ArrayList<Sport> sportList;
    HashMap<String,Integer> selectedSports;
    private OuterLayout outerLayout;
    int counter = 0;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    boolean filtered = false;

    public void setSlidingUpPanelLayout(SlidingUpPanelLayout panelLayout) {
        this.slidingUpPanelLayout = panelLayout;
    }

    public void setOuterLayout(OuterLayout outerLayout){
        this.outerLayout = outerLayout;
    }

    public SportListAdapter(Context context, int resource, List<Sport> objects) {
        super(context, resource, objects);
        this.context = context;
        this.sportList = (ArrayList<Sport>) objects;
    }

    public void setSelectedList(HashMap<String,Integer> selectedSports) {
        this.selectedSports = selectedSports;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("SportListAdapter", "position:"+ position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View elementView = inflater.inflate(R.layout.sport_select_item, parent, false);

        SvgImageTask imageTask = new SvgImageTask(context);
        final ImageView iconView = (ImageView) elementView.findViewById(R.id.sport_icon);
        iconView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iconView.setAdjustViewBounds(true);
        final Bitmap iconBitmap = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        TextView title = (TextView) elementView.findViewById(R.id.name);


        //grayscale filter
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(0);
        final ColorMatrixColorFilter grayFilter = new ColorMatrixColorFilter(grayMatrix);
        //color filter
        ColorMatrix colorMatrix = new ColorMatrix();
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

        iconView.setColorFilter(grayFilter);
        Sport sport = sportList.get(position);
        imageTask.onImageReady(new ImageReady(){
            @Override
            public void ready(Image image) {
                try {
                    SVG iconsvg = SVG.getFromString(image.content);

                    Canvas canvas = new Canvas(iconBitmap);
                    iconsvg.renderToCanvas(canvas);
                    iconView.setImageBitmap(iconBitmap);
                } catch (SVGParseException e) {
                    e.printStackTrace();
                }
            }
        });

        imageTask.execute(sport.getIconid());
        title.setText(sport.getName().toUpperCase());

        Log.i("AREAADAPTER", "called");
        if( this.selectedSports != null && this.selectedSports.get(sport.getId()) != null){

            elementView.setBackgroundColor(Color.parseColor("#0000ff"));
        }else {

            elementView.setBackgroundColor(Color.parseColor("#00ffffff"));
        }

        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) v).setColorFilter(colorFilter);
                Log.i("OuterLayout", "onClick");
                if(slidingUpPanelLayout != null && filtered == false){
                    filtered = true;
                    slidingUpPanelLayout.setAnchorPoint(.3f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
            }
        });
        return elementView;
    }
}
