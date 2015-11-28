package toxz.me.whizz.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.koushikdutta.async.http.server.AsyncHttpServer;

import toxz.me.whizz.application.MyBinder;

/**
 * Created by Carlos on 11/28/15.
 */
public class Server extends Service {
    public Server() {
        AsyncHttpServer server = new AsyncHttpServer();
        server.get(
                "/", (request, response) -> {
                    response.send("Hello World!");
                });
        server.listen(4567);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(this);
    }
}
