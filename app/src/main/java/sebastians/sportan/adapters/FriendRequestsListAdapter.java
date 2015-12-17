package sebastians.sportan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.ArrayList;
import java.util.List;

import sebastians.sportan.fragments.FriendItemFragment;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

/**
 * Created by sebastian on 14/12/15.
 */
public class FriendRequestsListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> areaList;
    public FriendRequestsListAdapter(Context context, int resource, List<String> objects) {
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
        friendItemFragment.setYesBtnTxt("Accept");
        friendItemFragment.setNoBtnTxt("Decline");
        friendItemFragment.setFriendItemInteraction(new FriendItemFragment.FriendItemInteraction() {
            @Override
            public void onNoButtonClicked() {
                final CustomAsyncTask noTask = new CustomAsyncTask(context);
                noTask.setTaskCallBacks(new TaskCallBacks() {
                    @Override
                    public String doInBackground() {
                        MyCredentials myCredentials = new MyCredentials(context);
                        try {
                            TMultiplexedProtocol mp = null;
                            mp = noTask.openTransport(SuperAsyncTask.SERVICE_USER);
                            UserSvc.Client client = new UserSvc.Client(mp);
                            client.declineFriendRequest(myCredentials.getToken(), areaList.get(position));
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
                        areaList.remove(position);
                        FriendRequestsListAdapter.this.notifyDataSetChanged();

                    }
                });
                noTask.execute();
            }

            @Override
            public void onYesButtonClicked() {
                final CustomAsyncTask yesTask = new CustomAsyncTask(context);
                yesTask.setTaskCallBacks(new TaskCallBacks() {
                    @Override
                    public String doInBackground() {
                        MyCredentials myCredentials = new MyCredentials(context);
                        try {
                            TMultiplexedProtocol mp = null;
                            mp = yesTask.openTransport(SuperAsyncTask.SERVICE_USER);
                            UserSvc.Client client = new UserSvc.Client(mp);
                            client.acceptFriendRequest(myCredentials.getToken(),areaList.get(position));
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
                        areaList.remove(position);
                        FriendRequestsListAdapter.this.notifyDataSetChanged();

                    }
                });
                yesTask.execute();

            }
        });
        return friendItemFragment.onCreateView(inflater,parent,null);

    }

}
