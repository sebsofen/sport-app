package sebastians.sportan.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;

import sebastians.sportan.R;
import sebastians.sportan.adapters.FriendRequestsListAdapter;
import sebastians.sportan.adapters.FriendsListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

/**
 *
 */
public class MainFriendsFragment extends Fragment {

    public static MainFriendsFragment newInstance() {
        MainFriendsFragment fragment = new MainFriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String receivedUserId = getActivity().getIntent().getStringExtra("USER");
        View view = inflater.inflate(R.layout.activity_friends, container, false);
        if(receivedUserId != null && !receivedUserId.equals("")){
            if((MyCredentials.Me.friends == null || !MyCredentials.Me.friends.contains(receivedUserId)) && (MyCredentials.Me.friendrequests == null || !MyCredentials.Me.friendrequests.contains(receivedUserId))) {
                final FriendItemFragment friendItemFragment = new FriendItemFragment();
                friendItemFragment.setUserId(receivedUserId);
                friendItemFragment.setYesBtnTxt("Add Friend");
                friendItemFragment.setFriendItemInteraction(new FriendItemFragment.FriendItemInteraction() {
                    @Override
                    public void onNoButtonClicked() {
                    }

                    @Override
                    public void onYesButtonClicked() {
                        Log.i("friendrequest", "yes clicked");
                        final CustomAsyncTask requestFriendTask = new CustomAsyncTask(getActivity());
                        requestFriendTask.setTaskCallBacks(new TaskCallBacks() {
                            @Override
                            public String doInBackground() {
                                MyCredentials myCredentials = new MyCredentials(getActivity());
                                try {
                                    TMultiplexedProtocol mp = null;
                                    mp = requestFriendTask.openTransport(SuperAsyncTask.SERVICE_USER);
                                    UserSvc.Client client = new UserSvc.Client(mp);
                                    client.sendFriendRequest(myCredentials.getToken(), receivedUserId);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            public void onPreExecute() {

                            }

                            @Override
                            public void onPostExecute() {
                                getActivity().getFragmentManager().beginTransaction().remove(friendItemFragment).commit();
                            }
                        });
                        requestFriendTask.execute();
                    }
                });
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction().replace(R.id.friend_container, friendItemFragment);
                transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
                transaction.commit();
            }

        }

        //get friends request list
        ListView friends_lst = (ListView) view.findViewById(R.id.friends_list);
        ListView friend_requests_lst = (ListView) view.findViewById(R.id.friend_requests_list);
        //set adapter for list!
        final ArrayList<String> friendRequestsList = new ArrayList<>();
        friendRequestsList.addAll(MyCredentials.Me.friendrequests != null ? MyCredentials.Me.friendrequests : new ArrayList<String>());
        final FriendRequestsListAdapter friendRequestsListAdapter = new FriendRequestsListAdapter(getActivity(),R.id.friend_requests_list,friendRequestsList);
        friend_requests_lst.setAdapter(friendRequestsListAdapter);

        final ArrayList<String> friendsList = new ArrayList<>();
        friendsList.addAll(MyCredentials.Me.friends != null ? MyCredentials.Me.friends : new ArrayList<String>());
        final FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getActivity(),R.id.friends_list,friendsList);
        friends_lst.setAdapter(friendsListAdapter);





        return view;
    }
}
