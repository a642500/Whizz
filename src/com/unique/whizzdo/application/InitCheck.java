package com.unique.whizzdo.application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import com.unique.whizzdo.monitor.NoticeMonitorService;

import java.util.List;

/**
 * a util class tp check whether account has been created && IMEI ...
 */
public class InitCheck {

    /**
     * @return 1 when the account is correct. return 0 when there is no user's data.
     */
    protected int isCorrect(Context context, SharedPreferences sharedPreferences) {
        String account = sharedPreferences.getString("account", null);
        if (account == null) return 0;
        return 1;
    }

    protected boolean isNoticeServiceRunning(Context context) {
        return isServiceRunning(context, NoticeMonitorService.class.getName());
    }

    private static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (serviceList == null || serviceList.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo aServiceList : serviceList) {
            if (aServiceList.service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


}
