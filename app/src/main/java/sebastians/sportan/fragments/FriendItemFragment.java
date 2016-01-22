package sebastians.sportan.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import sebastians.sportan.R;
import sebastians.sportan.adapters.FriendDetailFragment;
import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.User;
import sebastians.sportan.tasks.GetUserTask;
import sebastians.sportan.tasks.SetUserTask;

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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendDetailFragment friendDetailFragment = new FriendDetailFragment();
                friendDetailFragment.setFriend(friend);
                //FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                //ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_in);
                //ft.addToBackStack(null);
                //ft.replace(R.id.placeholder, friendDetailFragment).commit();
            }
        });

        final TextView username_txt = (TextView) view.findViewById(R.id.username_txt);
        final Switch adm = (Switch) view.findViewById(R.id.friend_adm);
        myCredentials = new MyCredentials(context == null? getActivity(): context);

            final GetUserTask getUserTask = new GetUserTask(context == null ? getActivity() : context, userId, new GetUserTask.OnPostExecute() {
                @Override
                public void onPostExectute(User user) {
                    friend = user;
                    username_txt.setText(user.getProfile().getUsername());
                        adm.setChecked((myCredentials.amISuperAdmin() && user.getRole() != null && ( user.getRole().equals("admin"))));

                }
            });
            getUserTask.execute();


        Button yesBtn = (Button) view.findViewById(R.id.yes_btn);
        Button noBtn = (Button) view.findViewById(R.id.no_btn);


        if(!myCredentials.amISuperAdmin() || myCredentials.Me.getIdentifier().equals(userId)){
            adm.setVisibility(View.GONE);
        }else{
            adm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SetUserTask setUserTask = new SetUserTask(context, userId);
                    setUserTask.setIsAdmin(isChecked);
                    setUserTask.execute();
                }
            });
        }

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


