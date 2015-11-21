package sebastians.sportan.tasks;

import android.content.Context;

/**
 * Created by sebastian on 09/11/15.
 */
public class CustomAsyncTask extends SuperAsyncTask {
    TaskCallBacks taskCallBacks;

    public CustomAsyncTask(Context ctx) {
        super(ctx);
    }

    @Override
    protected String doInBackground(String... strings) {

        String ret = taskCallBacks.doInBackground();
        closeTransport();
        return ret;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        taskCallBacks.onPostExecute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        taskCallBacks.onPreExecute();
    }
    public void setTaskCallBacks(TaskCallBacks taskCallBacks){
        this.taskCallBacks = taskCallBacks;
    }

}
