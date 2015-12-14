package sebastians.sportan;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.adapters.FriendRequestsListAdapter;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class FriendsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Uri data = getIntent().getData();
        if(data != null){
            List<String> params = data.getPathSegments();
            final String receivedUserId = params.get(params.size() - 1);
            final FriendItemFragment friendItemFragment = new FriendItemFragment();
            friendItemFragment.setUserId(receivedUserId );
            friendItemFragment.setYesBtnTxt("Add Friend");
            friendItemFragment.setFriendItemInteraction(new FriendItemFragment.FriendItemInteraction() {
                @Override
                public void onNoButtonClicked() {
                }

                @Override
                public void onYesButtonClicked() {
                    Log.i("friendrequest", "yes clicked");
                    final CustomAsyncTask requestFriendTask = new CustomAsyncTask(FriendsActivity.this);
                    requestFriendTask.setTaskCallBacks(new TaskCallBacks() {
                        @Override
                        public String doInBackground() {
                            MyCredentials myCredentials = new MyCredentials(FriendsActivity.this);
                            try {
                                TMultiplexedProtocol mp = null;
                                mp = requestFriendTask.openTransport(SuperAsyncTask.SERVICE_USER);
                                UserSvc.Client client = new UserSvc.Client(mp);
                                client.sendFriendRequest(myCredentials.getToken(),receivedUserId);
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
                            getFragmentManager().beginTransaction().remove(friendItemFragment).commit();
                        }
                    });
                    requestFriendTask.execute();
                }
            });
            getFragmentManager().beginTransaction().replace(R.id.friend_container, friendItemFragment).commit();
        }

        //get friends request list
        ListView friends_lst = (ListView) findViewById(R.id.friends_list);
        ListView friend_requests_lst = (ListView) findViewById(R.id.friend_requests_list);
        //set adapter for list!
        final ArrayList<String> friendRequestsList = new ArrayList<>();
        friendRequestsList.addAll(MyCredentials.Me.friendrequests);
        final FriendRequestsListAdapter friendRequestsListAdapter = new FriendRequestsListAdapter(this,R.id.friend_requests_list,friendRequestsList);
        friend_requests_lst.setAdapter(friendRequestsListAdapter);






    }

}

