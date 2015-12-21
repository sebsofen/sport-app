package sebastians.sportan.tasks;

import android.content.Context;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.UserSvc;

/**
 * Created by sebastian on 20/12/15.
 */
public class SetUserTask extends SuperAsyncTask {
    boolean isadmin_u = false;
    boolean isadmin = false;
    String userid;
    MyCredentials myCredentials;
    public SetUserTask(Context ctx, String userid) {
        super(ctx);
        this.userid = userid;
        this.myCredentials = new MyCredentials(ctx);
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isadmin = isAdmin;
        isadmin_u = true;
    }

    protected String doInBackground(String... strings) {

        try {
            TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_USER);
            UserSvc.Client client = new UserSvc.Client(mp);
            if(isadmin_u){
                client.setAdmin(myCredentials.getToken(),this.userid,isadmin);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transport.close();
        }
        return null;
    }


}
