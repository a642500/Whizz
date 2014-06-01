package com.unique.whizzdo.application;

import android.app.Application;
import android.content.*;
import android.os.IBinder;
import android.util.Log;
import com.unique.whizzdo.monitor.NoticeMonitorService;

import java.util.Random;

/**
 * initialize the account and database helper .start monitor service.
 */
public class MyApplication extends Application {

    public static final int LOCAL_ACCOUNT = 0;
    private NoticeMonitorService mNoticeMonitorService;

    @Override
    public void onTerminate() {
        Log.i("MyApplication", "onTerminate()");
        super.onTerminate();
    }

    public void newAccount(int kind) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("main_info", Context.MODE_PRIVATE);
        switch (kind) {
            case 0:
                int random = 0;
                for (; random == 0; ) {
                    random = new Random().nextInt();
                }
                random = Math.abs(random);
                sharedPreferences.edit().putString("account", String.valueOf(random)).commit();
                break;
            default:

        }
        ShortCutCreator.addShortcut(this, ShortCutCreator.NEW_TEXT_NOTE_SHORTCUT);
        notifyInit();
    }


    public int notifyInit() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("main_info", Context.MODE_PRIVATE);
        InitCheck checker = new InitCheck();
        if (!checker.isNoticeServiceRunning(this))
            startNoticeService();

        int result = checker.isCorrect(this, sharedPreferences);
        if (result == 1)
            init(sharedPreferences);

        return result;
    }

    private void init(SharedPreferences sharedPreferences) {
        AccountInfo.setAccount(sharedPreferences.getString("account", null));
    }


    @Override
    public void onCreate() {
        super.onCreate();
        notifyInit();
    }

    private void startNoticeService() {
        Intent intent = new Intent("com.unique.whizzdo.notice.NoticeMonitorService");
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mNoticeMonitorService = (NoticeMonitorService) ((MyBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    public NoticeMonitorService getNoticeMonitorService() {
        return mNoticeMonitorService;
    }

    public static class AccountInfo {
        static String account;

        public static String getAccount() {
            return account;
        }

        protected static void setAccount(String account) {
            assert account != null;
            AccountInfo.account = account;
        }

    }


}
