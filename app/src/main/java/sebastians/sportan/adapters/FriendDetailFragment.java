package sebastians.sportan.adapters;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sebastians.sportan.R;
import sebastians.sportan.networking.User;

/**
 * Created by sebastian on 22/01/16.
 */
public class FriendDetailFragment extends Fragment {
    private String userid;
    User friend;
    TextView friend_name;
    public FriendDetailFragment(){

    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_detail, container, false);
        friend_name = (TextView) view.findViewById(R.id.friend_name);

                friend_name.setText(friend.getProfile().getUsername());

        return view;
    }
}
