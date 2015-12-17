package sebastians.sportan;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.User;
import sebastians.sportan.tasks.GetUserTask;

/**
 * Created by sebastian on 13/12/15.
 */
public class FriendItemFragment extends Fragment {
    FriendItemInteraction friendItemInteraction = null;
    MyCredentials myCredentials;
    Context context;
    public void setNoBtnTxt(String noBtnTxt) {
        this.noBtnTxt = noBtnTxt;
    }

    public void setYesBtnTxt(String yesBtnTxt) {
        this.yesBtnTxt = yesBtnTxt;
    }

    String yesBtnTxt = null;
    String noBtnTxt = null;
    String userId = null;
    User friend;
    public void setFriendItemInteraction(FriendItemInteraction friendItemInteraction){
        this.friendItemInteraction = friendItemInteraction;
    }

    public void setUserId(String userid) {
        this.userId = userid;
    }

    public void setContext(Context ctx){
        context = ctx;
    }

    public FriendItemFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_item, container, false);
        final TextView username_txt = (TextView) view.findViewById(R.id.username_txt);

        myCredentials = new MyCredentials(context == null? getActivity(): context);

            final GetUserTask getUserTask = new GetUserTask(context == null ? getActivity() : context, userId, new GetUserTask.OnPostExecute() {
                @Override
                public void onPostExectute(User user) {
                    friend = user;
                    username_txt.setText(user.getProfile().getUsername());
                }
            });
            getUserTask.execute();


        Button yesBtn = (Button) view.findViewById(R.id.yes_btn);
        Button noBtn = (Button) view.findViewById(R.id.no_btn);

        if(yesBtnTxt == null){
            yesBtn.setVisibility(View.GONE);
        }else{
            yesBtn.setText(yesBtnTxt);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(friendItemInteraction != null){
                        friendItemInteraction.onYesButtonClicked();
                    }
                }
            });
        }
        if(noBtnTxt == null){
            noBtn.setVisibility(View.GONE);
        }else{
            noBtn.setText(noBtnTxt);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(friendItemInteraction != null){
                        friendItemInteraction.onNoButtonClicked();
                    }
                }
            });
        }


        return view;
    }

    public interface FriendItemInteraction {
        void onNoButtonClicked();
        void onYesButtonClicked();

    }
}


