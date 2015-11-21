package sebastians.sportan;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import sebastians.sportan.networking.ThriftToken;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.UserCreationTask;

/**
 * Created by sebastian on 29/10/15.
 */
public class MyCredentials {
    public static final String USERCREDENTIALS_IDENTIFIER = "identifier";
    public static final String USERCREDENTIALS_PASSWORD = "password";
    public static final String USERCREDENTIALS_TOKENVALIDITY = "tokenvalidity";
    public static final String USERCREDENTIALS_TOKEN = "token";
    private String identifier;
    private String password;
    String host;
    int port;
    private long tokenValidity;
    private String token;
    SharedPreferences sharedPref;

    public MyCredentials(Context ctx){
        host = ctx.getString(R.string.host);
        port = Integer.parseInt(ctx.getString(R.string.port));
        sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        if("".equals(sharedPref.getString(USERCREDENTIALS_IDENTIFIER,"")) && "".equals(sharedPref.getString(USERCREDENTIALS_PASSWORD,""))){
            UserCreationTask userCreationTask = new UserCreationTask(ctx);
            userCreationTask.execute("");
        }else{
            identifier = sharedPref.getString(USERCREDENTIALS_IDENTIFIER,"");
            password = sharedPref.getString(USERCREDENTIALS_PASSWORD,"");

        }


    }

    public boolean isTokenExpired(){
        return tokenValidity < System.currentTimeMillis();
    }

    public String getToken(){
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

            ThriftToken thriftToken = client.requestToken(this.getIdentifier(),this.getPassword());
            //read and transform token information
            String token = thriftToken.getToken();
            long validity =System.currentTimeMillis() + thriftToken.getValidityDuration();
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(MyCredentials.USERCREDENTIALS_TOKEN, token);
            edit.putLong(MyCredentials.USERCREDENTIALS_TOKENVALIDITY, validity);
            edit.apply();

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

}
