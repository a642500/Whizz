package com.unique.whizzdo.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.*;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import com.unique.whizzdo.additional.QuickSnapService;
import com.unique.whizzdo.monitor.NoticeMonitorService;

import java.util.List;
import java.util.Random;


/**
 * initialize the account and database helper .start monitor service.
 */
public class MyApplication extends Application {
    public static final int LOCAL_ACCOUNT = 0;
    private Initiator mInitiator = new Initiator();
    private NoticeMonitorService mNoticeMonitorService;
    private QuickSnapService mQuickSnapService;

    @Override
    public void onTerminate() {
        Log.i("MyApplication", "onTerminate()");
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInitiator.notifyInit();
    }

    public void newAccount(int kind) {
        mInitiator.newAccount(kind);
    }

    public NoticeMonitorService getNoticeMonitorService() {
        return mNoticeMonitorService;
    }

    public static class AccountInfo {
        static String account;

        public static String getAccount() {
            return account;
        }

        static void setAccount(String account) {
            assert account != null;
            AccountInfo.account = account;
        }

    }


    public class Initiator {

        public void notifyInit() {
            InitCheck checker = new InitCheck();

            //初始化账号问题，
            int result = checker.isCorrect();
            if (result == 1)
                initAccount();

            //初始化各项服务
            if (!checker.isServiceRunning(NoticeMonitorService.class.getName()))
                startNoticeService();
            if (!checker.isServiceRunning(QuickSnapService.class.getName()))
                startQuickSnapService();

        }


        protected void initAccount() {
            SharedPreferences sharedPreferences = getSharedPreferences("main_info", Context.MODE_PRIVATE);
            MyApplication.AccountInfo.setAccount(sharedPreferences.getString("account", null));
        }

        public void newAccount(int kind) {
            SharedPreferences sharedPreferences = getSharedPreferences("main_info", Context.MODE_PRIVATE);
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
            ShortCutCreator.addShortcut(MyApplication.this, ShortCutCreator.NEW_TEXT_NOTE_SHORTCUT);
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

        private void startQuickSnapService() {
            Intent intent = new Intent("com.unique.whizzdo.additional.QuickSnapService");
            startService(intent);
            bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mQuickSnapService = (QuickSnapService) ((MyBinder) service).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            }, BIND_AUTO_CREATE);
        }
    }

    public class InitCheck {
        /**
         * @return 1 when the account is correct. return 0 when there is no user's data.
         */
        protected int isCorrect() {
            SharedPreferences sharedPreferences = getSharedPreferences("main_info", Context.MODE_PRIVATE);
            String account = sharedPreferences.getString("account", null);
            if (account == null) return 0;
            return 1;
        }

        private boolean isServiceRunning(String className) {
            boolean isRunning = false;
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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

}
