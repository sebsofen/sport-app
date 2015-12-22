package sebastians.sportan.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import sebastians.sportan.R;
import sebastians.sportan.networking.Profile;
import sebastians.sportan.networking.ServiceConstants;
import sebastians.sportan.networking.Token;
import sebastians.sportan.networking.User;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.CustomAsyncTask;
import sebastians.sportan.tasks.SuperAsyncTask;
import sebastians.sportan.tasks.TaskCallBacks;
import sebastians.sportan.tasks.UserCreationTask;
import sebastians.sportan.tools.TaskFinishInterface;

/**
 * Created by sebastian on 29/10/15.
 */
public class MyCredentials implements TaskFinishInterface {
    public static final String USERCREDENTIALS_IDENTIFIER = "identifier";// + "j";
    public static final String USERCREDENTIALS_PASSWORD = "password";// + "j";
    public static final String USERCREDENTIALS_TOKENVALIDITY = "tokenvalidity";// + "j";
    public static final String USERCREDENTIALS_TOKEN = "token";// + "j";
    private String identifier;
    private String password;
    String host;
    int port;
    private static long tokenValidity;
    private static String token;
    static SharedPreferences sharedPref;
    public static User Me;
    MyCredentialsFinishedCallBack myCredentialsFinishedCallBack;
    Context ctx;

    public MyCredentials(Context ctx, MyCredentialsFinishedCallBack myCredentialsFinishedCallBack){
        host = ctx.getString(R.string.host);
        port = Integer.parseInt(ctx.getString(R.string.port));

        if(sharedPref == null)
            sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        this.ctx = ctx;
        this.myCredentialsFinishedCallBack = myCredentialsFinishedCallBack;
        if("".equals(sharedPref.getString(USERCREDENTIALS_IDENTIFIER,"")) && "".equals(sharedPref.getString(USERCREDENTIALS_PASSWORD,""))){
            Log.i("MyCredentials", "user empty");
            UserCreationTask userCreationTask = new UserCreationTask(ctx, this);
            userCreationTask.execute("");
        }else{
            getMe();
        }
    }


    public void getMe() {
        getMe(false);
    }

    /**
     * get myself from server and save information in Me variable
     */
    public void getMe(boolean force){
        identifier = sharedPref.getString(USERCREDENTIALS_IDENTIFIER,"");
        password = sharedPref.getString(USERCREDENTIALS_PASSWORD,"");

        //load user details in static object
        if(Me == null || force){
            final CustomAsyncTask gatherInformationTask = new CustomAsyncTask(ctx);
            gatherInformationTask.setTaskCallBacks(
                    new TaskCallBacks() {
                        User user;
                        @Override
                        public String doInBackground() {
                            TMultiplexedProtocol mp = null;
                            try {
                                mp = gatherInformationTask.openTransport(SuperAsyncTask.SERVICE_USER);
                                UserSvc.Client client = new UserSvc.Client(mp);
                                user = client.getMe(getToken());
                                gatherInformationTask.closeTransport();
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
                            MyCredentials.Me = user;
                            //TODO ! VERIFY USER HERE!
                            if(Me != null && Me.getProfile() == null){
                                Me.setProfile(new Profile());
                            }

                            if(myCredentialsFinishedCallBack != null){
                                myCredentialsFinishedCallBack.onFinish();
                            }
                        }
                    }
            );
            gatherInformationTask.execute("");

        }else{
            if(myCredentialsFinishedCallBack != null)
                myCredentialsFinishedCallBack.onFinish();
        }
    }


    public MyCredentials(Context ctx){
        this(ctx,null);
    }

    /**
     * will tell, if user is  admin!
     * @return
     */
    public  boolean amIAdmin() {
        return (MyCredentials.Me != null && MyCredentials.Me.getRole() != null  && (MyCredentials.Me.getRole().equals(ServiceConstants.ROLE_ADMIN) || MyCredentials.Me.getRole().equals(ServiceConstants.ROLE_SUPERADMIN)));
    }

    public boolean amISuperAdmin() {
        return (MyCredentials.Me != null && MyCredentials.Me.getRole() != null  && MyCredentials.Me.getRole().equals(ServiceConstants.ROLE_SUPERADMIN));
    }


    //TODO change to more realistic thing!
    public boolean isTokenExpired(){
        return System.currentTimeMillis() - 5000 > this.tokenValidity;

    }

    public String getToken(){
        if(this.isTokenExpired())
            renewToken();

        return this.token;
    }

    /**
     * renoew token, if expired
     * this will only be done in async tasks!
     */
    public void renewToken(){

        TTransport transport= new TFramedTransport(new TSocket(host,port));
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, "User");
            UserSvc.Client client = new UserSvc.Client(mp);
            Token thriftToken = client.requestToken(this.getIdentifier(),this.getPassword());
            String token = thriftToken.getToken();
            long validity =System.currentTimeMillis() + thriftToken.getValidity();
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(MyCredentials.USERCREDENTIALS_TOKEN, token);
            edit.putLong(MyCredentials.USERCREDENTIALS_TOKENVALIDITY, validity);
            edit.apply();
            Log.i("MyCredentials", "calc token from" + validity);
            this.tokenValidity = validity;
            this.token = token;
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            transport.close();
        }


    }



    public String getIdentifier(){
        return this.identifier;

    }

    public String getPassword(){
        return this.password;
    }

    @Override
    public void onFinish(boolean success) {
        getMe();
    }
}
