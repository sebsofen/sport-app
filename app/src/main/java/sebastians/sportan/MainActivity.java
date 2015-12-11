package sebastians.sportan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.fragments.SelectCityFragment;
import sebastians.sportan.networking.City;


public class MainActivity extends ActionBarActivity implements SelectCityFragment.SelectedCityListener {
    public MyCredentials myCredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myCredentials = new MyCredentials(this);
        Log.i("Main", "mycreds " + myCredentials.getIdentifier());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //launch profileactivity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void citySelected(City city) {

    }
}
