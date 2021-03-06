package sebastians.sportan.tasks;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import java.util.Random;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.R;
import sebastians.sportan.networking.InvalidOperation;
import sebastians.sportan.networking.UserCredentials;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tools.TaskFinishInterface;

/**
 * Created by sebastian on 29/10/15.
 */
public class UserCreationTask extends SuperAsyncTask {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    SharedPreferences sharedPreferences;
    TaskFinishInterface taskFinishInterface;
    boolean succeded = true;
    public UserCreationTask(Context ctx){
        super(ctx);
        sharedPreferences = ctx.getSharedPreferences(
                ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public UserCreationTask(Context ctx,TaskFinishInterface taskFinishInterface){
        this(ctx);
        this.taskFinishInterface = taskFinishInterface;
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_USER);

            UserSvc.Client client = new UserSvc.Client(mp);
            String myPassword = getRandomString();
            UserCredentials userIdent = client.createUser(myPassword);
            String myIdent = userIdent.getIdentifier();
            //save usercredentials
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(MyCredentials.USERCREDENTIALS_IDENTIFIER, myIdent);
            editor.putString(MyCredentials.USERCREDENTIALS_PASSWORD, myPassword);

            editor.commit();

            //set in shared preferences

        } catch (InvalidOperation x) {
            succeded = false;
            x.printStackTrace();
        } catch (Exception x) {
            succeded = false;
            x.printStackTrace();
        } finally {
            transport.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //save user identification in variable
        if(this.taskFinishInterface != null)
            this.taskFinishInterface.onFinish(succeded);
    }

    public static String getRandomString(){
        Random rnd = new Random(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder( 20 );
        for( int i = 0; i < 20; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }



}
