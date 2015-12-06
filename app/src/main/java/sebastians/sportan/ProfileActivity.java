package sebastians.sportan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.fragments.SelectCityFragment;
import sebastians.sportan.networking.City;
import sebastians.sportan.networking.Profile;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class ProfileActivity extends ActionBarActivity implements SelectCityFragment.OnFragmentInteractionListener {
    ImageButton edit_username_button;
    TextView edit_username_text;
    SwipeRefreshLayout swipeRefresh;
    ImageButton share_btn;
    ImageButton select_city_btn;
    EditText user_id_txt;
    EditText city_name_txt;
    SelectCityFragment select_city_fragment;
    MyCredentials myCredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edit_username_button = (ImageButton)findViewById(R.id.edit_button);
        final ProfileActivity mThis = this;
        edit_username_text = (TextView)findViewById(R.id.username);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);


        share_btn = (ImageButton) findViewById(R.id.share_btn);
        select_city_btn = (ImageButton) findViewById(R.id.select_city_btn);

        user_id_txt = (EditText) findViewById(R.id.user_id_txt);
        city_name_txt = (EditText) findViewById(R.id.city_name_txt);


        myCredentials = new MyCredentials(this);
        user_id_txt.setText(myCredentials.getIdentifier());



        //share userid ;_)
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, "I want to be friends with you on sportan: http://www.sportanapp.com/friends/" + myCredentials.getIdentifier());
                startActivity(Intent.createChooser(intent2, "Share via"));
            }
        });

        select_city_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ProfileActivity", "start fragment");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectCityFragment()).commit();
            }
        });



        edit_username_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Profile", "usernameedit");

                final AlertDialog.Builder inputAlert = new AlertDialog.Builder(mThis);
                inputAlert.setTitle("Title of the Input Box");
                inputAlert.setMessage("We need your name to proceed");
                final EditText userInput = new EditText(mThis);
                inputAlert.setView(userInput);
                inputAlert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String userInputValue = userInput.getText().toString();
                        mThis.updateUsername(userInputValue);
                        final CustomAsyncTask transmit = new CustomAsyncTask(mThis);
                        transmit.setTaskCallBacks(new TaskCallBacks() {
                            @Override
                            public String doInBackground() {
                                try {
                                    MyCredentials myCredentials = new MyCredentials(mThis);
                                    if (myCredentials.isTokenExpired()) {
                                        myCredentials.renewToken();
                                    }
                                    TMultiplexedProtocol mp = transmit.openTransport(SuperAsyncTask.SERVICE_USER);
                                    UserSvc.Client client = new UserSvc.Client(mp);
                                    Profile profile = new Profile();
                                    profile.setUsername(userInputValue);
                                    client.setProfile(myCredentials.getToken(), profile);
                                    Log.i("ProfileActivity","new username set");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                            @Override
                            public void onPreExecute() {
                                swipeRefresh.setRefreshing(true);
                            }
                            @Override
                            public void onPostExecute() {
                                swipeRefresh.setRefreshing(false);
                            }
                        });

                        transmit.execute("");
                    }
                });
                inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = inputAlert.create();
                alertDialog.show();


            }
        });


    }

    protected void updateUsername(String username){
        //update stuff here!
        Log.i("Profile", "username " + username);
        edit_username_text.setText(username);
    }


    @Override
    public void onFragmentInteraction(City city) {
        Log.i("ProfileActivity", "City set");
        city_name_txt.setText(city.getName());
    }
}
