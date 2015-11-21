package sebastians.sportan.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import sebastians.sportan.R;

/**
 * Created by sebastian on 29/10/15.
 */
public class SuperAsyncTask extends AsyncTask<String,String,String>{
    public static final String SERVICE_USER = "User";
    public static final String SERVICE_AREA = "Area";
    public static final String SERVICE_CITY = "City";
    public static final String SERVICE_SPORT = "Sport";
    public static final String SERVICE_IMAGE = "Image";
    String host;
    int port;
    TTransport transport;
    public SuperAsyncTask(Context ctx){
        host = ctx.getString(R.string.host);
        port = Integer.parseInt(ctx.getString(R.string.port));
        transport = new TFramedTransport(new TSocket(host,port));
    }

    public TMultiplexedProtocol openTransport(String ident) throws Exception{
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, ident);
        return mp;
    }

    public void closeTransport(){
        transport.close();
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }
}
