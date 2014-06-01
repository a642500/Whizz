package com.unique.whizzdo.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.unique.whizzdo.data.DataChangedListener;

/**
 * Created by Carlos on 4/16/2014.
 */
public class SyncMonitorService extends Service  implements DataChangedListener{
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void notifyDataChanged() {

    }
}
