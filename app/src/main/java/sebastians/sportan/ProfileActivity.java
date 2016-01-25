package sebastians.sportan;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.fragments.SelectCityFragment;
import sebastians.sportan.networking.City;
import sebastians.sportan.networking.Profile;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;
import sebastians.sportan.tools.ImageProcessing;

public class ProfileActivity extends ActionBarActivity
        implements SelectCityFragment.SelectedCityListener {

    private static final String TAG = "ProfileActivity";

    final int CHOOSE_PIC_REQUEST_CODE = 1;
    final int CROP_PIC_REQUEST_CODE = 2;

    RelativeLayout layoutPhoto;
    ImageView profile_photo_view;
    ImageButton profile_photo_edit;
    ImageButton edit_username_button;
    TextView edit_username_text;
//    SwipeRefreshLayout swipeRefresh;
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
//        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        select_city_btn = (ImageButton) findViewById(R.id.select_city_btn);
        city_name_txt = (TextView) findViewById(R.id.city_name_txt);

        myCredentials = new MyCredentials(this);

        edit_username_text.setText(MyCredentials.Me.getProfile().getUsername());

        Toolbar toolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Show the already set picture, if the user already has set and saved a profile picture
        Bitmap photo = loadBitmapFromDirectory("profile_pic.jpg");
        if(photo != null){
            Log.d(TAG, "photo != null");
            profile_photo_view.setImageBitmap(ImageProcessing.getRoundedCornerBitmap(photo, 7));
//            layoutPhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_dialog_photo_available));
            layoutPhoto.setBackgroundColor(Color.TRANSPARENT);
        }
        else{
            Log.d(TAG, "photo == null");
        }

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
                inputAlert.setTitle("Benutzer");
                inputAlert.setMessage("Wähle einen Titel");
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
//                                swipeRefresh.setRefreshing(true);
                            }

                            @Override
                            public void onPostExecute() {
//                                swipeRefresh.setRefreshing(false);
                            }
                        });

                        transmit.execute("asd");
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PIC_REQUEST_CODE:
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

                            // Crop
                            doCrop(getImageUri(this, bMap_image));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }

            case CROP_PIC_REQUEST_CODE:{
                if (data != null) {
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String filePath = cursor.getString(columnIndex);
                            cursor.close();
                            Bitmap bMap_image = BitmapFactory.decodeFile(filePath);
                            saveBitmapPersistent(bMap_image, "profile_pic.jpg");
//                            profile_photo_view.setImageBitmap(bMap_image);
                            profile_photo_view.setImageBitmap(ImageProcessing.getRoundedCornerBitmap(bMap_image, 7));
//                            layoutPhoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_dialog_photo_available));
                            layoutPhoto.setBackgroundColor(Color.TRANSPARENT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Log.d("ProfileActivity", "photouri == null");
                    }
                }
                break;
            }
        }
    }

    public void choosePicture() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory());
        startActivityForResult(photoPickerIntent, CHOOSE_PIC_REQUEST_CODE);
    }

    private void doCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
//            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory());
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

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

    /**
     * Get the directory, which the app uses for persistence file savings.
     *
     * @return The MEDIC folder which contains the taken pictures.
     */
    private File getSportanDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "SportanData");
    }

    public Bitmap loadBitmapFromDirectory(String fileNameWithEnding) {
        String photoPath = getSportanDir() + File.separator + fileNameWithEnding;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(photoPath, options);
    }

    /**
     * Save a bitmap persistent with a specific name.
     *
     * @param bmp   bitmap to be saved.
     * @param photoName The photonomae including the ending, example: pic1.png
     */
    private void saveBitmapPersistent(Bitmap bmp, String photoName){
        File pictureFileDir = getSportanDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d("WearMesLisService", "Can't create directory to save image.");
            Toast.makeText(this.getApplicationContext(), "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //photoName = "Picture_MedicTest.jpg";
        String fileName = pictureFileDir.getPath() + File.separator + photoName;
        File pictureFile = new File(fileName);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pictureFile); //fileName
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
