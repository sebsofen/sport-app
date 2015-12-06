package sebastians.sportan.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.R;
import sebastians.sportan.customviews.GeoContourView;
import sebastians.sportan.networking.City;
import sebastians.sportan.networking.Coordinate;

/**
 * Created by sebastian on 07/11/15.
 */
public class CityListAdapter extends ArrayAdapter<City> {
    Context context;
    ArrayList<City> areaList;
    Coordinate coord;
    public CityListAdapter(Context context, int resource, List<City> objects) {
        super(context, resource, objects);
        this.context = context;
        this.areaList = (ArrayList<City>) objects;
    }

    public void setUserLocation(Coordinate coord){
        this.coord = coord;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("CityListAdapter", "position:"+ position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.city_list_item, parent, false);
        TextView title = (TextView) view.findViewById(R.id.city_name);
        GeoContourView geoContourView = (GeoContourView) view.findViewById(R.id.geocontour);
        geoContourView.setContourList(areaList.get(position).coords);

        if (this.coord != null) {
           geoContourView.addPOI(this.coord, R.color.abc_primary_text_material_light);
        }


        geoContourView.invalidate();
        City city = areaList.get(position);
        title.setText(city.getName());

        return view;
    }
}
