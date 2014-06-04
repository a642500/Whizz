package com.unique.whizzdo.additional;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.unique.whizzdo.application.MyBinder;
import org.jetbrains.annotations.Nullable;

/**
 * Created by carlos on 6/3/14.
 */
public class QuickSnapService extends Service {
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceiver()", "receiver a broadcast screen on");
            handleBroadcastReceiver(context, intent);
        }
    };

    private void handleBroadcastReceiver(Context context, Intent intent) {
        
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder(this);
    }

    @Override
    public void onCreate() {
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        super.onCreate();
    }
}
