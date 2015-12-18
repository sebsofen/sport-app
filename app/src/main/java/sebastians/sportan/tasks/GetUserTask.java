package sebastians.sportan.tasks;

import android.content.Context;

import org.apache.thrift.protocol.TMultiplexedProtocol;

import sebastians.sportan.app.MyCredentials;
import sebastians.sportan.networking.User;
import sebastians.sportan.networking.UserSvc;
import sebastians.sportan.tasks.caches.UsersCache;


/**
 * Created by sebastian on 14/12/15.
 */
public class GetUserTask extends SuperAsyncTask {

    OnPostExecute onPostExecute = null;
    String userid;
    User user;
    MyCredentials myCredentials;
    public GetUserTask(Context ctx, String userid,OnPostExecute onPostExecute) {
        super(ctx);
        this.onPostExecute = onPostExecute;
        this.userid = userid;
        this.myCredentials = new MyCredentials(ctx);
    }



    protected String doInBackground(String... strings) {
        if(UsersCache.get(userid) != null){
            user = UsersCache.get(userid);
            return null;
        }
        try {
            TMultiplexedProtocol mp = openTransport(SuperAsyncTask.SERVICE_USER);
            UserSvc.Client client = new UserSvc.Client(mp);
            user = client.getUserById(myCredentials.getToken(),this.userid);
            UsersCache.add(userid,user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transport.close();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(onPostExecute != null){
            onPostExecute.onPostExectute(user);
        }
    }

    public interface OnPostExecute {
        void onPostExectute(User user);
    }
}
