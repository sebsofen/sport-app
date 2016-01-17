package sebastians.sportan;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ProfileActivity extends ActionBarActivity implements SelectCityFragment.SelectedCityListener {
    RelativeLayout layoutPhoto;
    ImageView profile_photo_view;
    ImageButton profile_photo_edit;
    ImageButton edit_username_button;
    TextView edit_username_text;
    SwipeRefreshLayout swipeRefresh;
    ImageButton select_city_btn;
    TextView city_name_txt;

    MyCredentials myCredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ProfileActivity mThis = this;

        layoutPhoto = (RelativeLayout) findViewById(R.id.layoutPhoto);
        profile_photo_view = (ImageView) findViewById(R.id.profile_photo_view);
        profile_photo_edit = (ImageButton) findViewById(R.id.profile_photo_edit);
        edit_username_button = (ImageButton)findViewById(R.id.edit_button);
        edit_username_text = (TextView)findViewById(R.id.username);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        select_city_btn = (ImageButton) findViewById(R.id.select_city_btn);
        city_name_txt = (TextView) findViewById(R.id.city_name_txt);

        myCredentials = new MyCredentials(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        layoutPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        profile_photo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
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
                                    TMultiplexedProtocol mp = transmit.openTransport(SuperAsyncTask.SERVICE_USER);
                                    UserSvc.Client client = new UserSvc.Client(mp);
                                    Profile profile = new Profile();
                                    profile.setUsername(userInputValue);
                                    client.setProfile(myCredentials.getToken(), profile);
                                    Log.i("ProfileActivity", "new username set");
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

        select_city_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ProfileActivity", "start fragment");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectCityFragment()).commit();
            }
        });
    }

    public void choosePicture() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
            {
                if (resultCode == RESULT_OK)
                {
                    Uri photoUri = data.getData();
                    if (photoUri != null)
                    {
                        try {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String filePath = cursor.getString(columnIndex);
                            cursor.close();
                            Bitmap bMap_image = BitmapFactory.decodeFile(filePath);
                            profile_photo_view.setImageBitmap(bMap_image);
                        }catch(Exception e)
                        {}
                    }
                }// resultCode
            }// case 1
        }// switch, request code
    }// public void onActivityResult

    protected void updateUsername(String username){
        //update stuff here!
        Log.i("Profile", "username " + username);
        edit_username_text.setText(username);
    }

    @Override
    public void citySelected(City city) {
        Log.i("ProfileActivity", "City set");
        city_name_txt.setText(city.getName());
    }
}
