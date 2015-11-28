package toxz.me.whizz.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import toxz.me.whizz.application.MyBinder;

/**
 * Created by Carlos on 11/28/15.
 */
public class Server extends Service {
    private static final String TAG = "HttpServer";
    private Runtime mRuntime = Runtime.getRuntime();

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AsyncHttpServer server = new AsyncHttpServer();
        route(server);
        server.listen(5000);

        Log.i(TAG, "server is open on:" + getIPAddress(true) + " port 5000");
    }

    private void route(AsyncHttpServer server) {
        server.get("/", (request, response) -> response.send("Hello World!"));

        server.get("/exec", (request, response) -> {
            Log.i(TAG, "request" + request.toString());
            String cmd = request.getQuery().getString("cmd");
            if (TextUtils.isEmpty(cmd)) {
                response.send("empty command");
            } else {
                try {
                    response.send("text/plain", run(cmd));
                } catch (IOException e) {
                    e.printStackTrace();
                    response.send(e.getMessage() + "---" + Arrays.toString(e.getStackTrace()));
                }
            }
            Log.i(TAG, "response: " + response.toString());
        });
        server.get("/push",(request, response) -> {
            String path = request.getQuery().getString("path");
            AsyncHttpRequestBody body =request.getBody();
        });
        server.get("/pull",(request, response) -> {
            String filePath = request.getQuery().getString("path");
            response.sendFile(new File(filePath));
        });
    }

    private String run(String cmd) throws IOException {
        Process process = mRuntime.exec(cmd);
        return IOUtils.toString(process.getInputStream());
    }


    public void RunAsRoot() throws IOException {
        String[] commands = {"sysrw", "rm /data/local/bootanimation.zip", "sysro"};

        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        for (String tmpCmd : commands) {
            os.writeBytes(tmpCmd + "\n");
        }
        os.writeBytes("exit\n");
        os.flush();
    }
}
