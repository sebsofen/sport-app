package sebastians.sportan;

import android.content.DialogInterface;
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

import sebastians.sportan.networking.UserProfile;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;

public class ProfileActivity extends ActionBarActivity {
    ImageButton edit_username_button;
    TextView edit_username_text;
    SwipeRefreshLayout swipeRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edit_username_button = (ImageButton)findViewById(R.id.edit_button);
        final ProfileActivity mThis = this;
        edit_username_text = (TextView)findViewById(R.id.username);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
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
                                    UserProfile profile = new UserProfile();
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

}
