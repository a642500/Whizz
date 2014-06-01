package com.unique.whizzdo.application;

import android.app.Service;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by carlos on 5/31/14.
 */
public class MyBinder extends Binder {
    Service mService;

    public MyBinder(Service service) {
        mService = service;
    }

    public Service getService() {
        return mService;
    }
}
