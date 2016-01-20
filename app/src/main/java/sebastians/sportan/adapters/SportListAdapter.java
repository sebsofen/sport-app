package sebastians.sportan.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.R;
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
    ArrayList<String> selectedSportsList = new ArrayList<>();
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private SportListSelectedFilter sportListSelectedFilter;
    boolean filtered = false;
    private boolean onClickLock = false;
    private boolean singleSelectionMode = false;


    public void resetFilter(){
        this.selectedSportsList.clear();
        notifyDataSetInvalidated();
    }

    public void setSlidingUpPanelLayout(SlidingUpPanelLayout panelLayout) {
        this.slidingUpPanelLayout = panelLayout;
    }

    public ArrayList<String> getSelectedSportsList() {
        return this.selectedSportsList;
    }
    public void setSportListSelectedFilter(SportListSelectedFilter sportListSelectedFilter){
        this.sportListSelectedFilter = sportListSelectedFilter;
    }

    public SportListAdapter(Context context, int resource, List<Sport> objects) {
        super(context, resource, objects);
        this.context = context;
        this.sportList = (ArrayList<Sport>) objects;
    }

    public void setSelectedList(ArrayList<String> selectedSports) {
        this.selectedSportsList = selectedSports;
    }

    /**
     * selection
     * @param selection
     */
    public void setSingleSelection(boolean selection) {
        singleSelectionMode = selection;
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
        final Bitmap iconBitmap = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888);


        //grayscale filter
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(.2f);
        final ColorMatrixColorFilter grayFilter = new ColorMatrixColorFilter(grayMatrix);
        //color filter
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(1.2f);
        final ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

        iconView.setColorFilter(grayFilter);
        final Sport sport = sportList.get(position);
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

        if( selectedSportsList.contains(sport.getId())){
            iconView.setColorFilter(colorFilter);
        }else {
            iconView.setColorFilter(grayFilter);
        }

        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickLock)
                    return;

                //logic stuff
                if(selectedSportsList.contains(sport.getId())){
                    selectedSportsList.remove(sport.getId());
                    ((ImageView) v).setColorFilter(grayFilter);

                }else{
                    selectedSportsList.add(sport.getId());
                     ((ImageView) v).setColorFilter(colorFilter);
                    if(singleSelectionMode == true){
                        ArrayList<String> filterlist = new ArrayList<String>();
                        filterlist.add(sport.getId());
                        selectedSportsList.retainAll(filterlist);
                        notifyDataSetChanged();
                    }

                }

                if(slidingUpPanelLayout != null && filtered == false){
                    filtered = true;
                    slidingUpPanelLayout.setAnchorPoint(.3f);
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }

                if(SportListAdapter.this.sportListSelectedFilter != null){
                    SportListAdapter.this.sportListSelectedFilter.filterChanged(selectedSportsList);
                }
            }
        });
        return elementView;
    }

    public String getSelectedSport(){
        if (selectedSportsList.size() > 0){
            return selectedSportsList.get(0);
        }else {
            return null;
        }
    }
}
