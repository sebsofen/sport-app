package sebastians.sportan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.fragments.FriendItemFragment;

/**
 * Created by sebastian on 14/12/15.
 */
public class FriendsListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> areaList;
    public FriendsListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.areaList = (ArrayList<String>) objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final FriendItemFragment friendItemFragment = new FriendItemFragment();
        friendItemFragment.setContext(context);
        friendItemFragment.setUserId(areaList.get(position));
        return friendItemFragment.onCreateView(inflater,parent,null);

    }

}
