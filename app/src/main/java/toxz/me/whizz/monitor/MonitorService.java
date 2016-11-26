package toxz.me.whizz.monitor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import toxz.me.whizz.data.DatabaseHelper;
import toxz.me.whizz.data.Note;

/**
 * Created by Carlos on 4/16/2014.
 */


public class MonitorService extends AccessibilityService {

    public static final String MY_ACTION_MAIN_ACTIVITY_EXIT = "com.unique.whizzdo.MainActivity" +
            ".EXIT";
    public static final int EVENT_SCREEN_ON = 0x10;
    public static final int EVENT_SCREEN_OFF = 0x11;
    public static final int EVENT_POWER_CONNECTED = 0x12;
    public static final int EVENT_POWER_DISCONNECTED = 0x13;
    public static final int EVENT_NETWORK_CHANGED = 0x14;
    public static final int EVENT_SWITCH_APP = 0x15;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceiver()", "receiver a broadcast");
            handleBroadcastReceiver(context, intent);
        }
    };

    private void handleBroadcastReceiver(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(MY_ACTION_MAIN_ACTIVITY_EXIT)) {
                //                post("Whizz will notify you background !", 5000);
            } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                //                post(action, 100);
            } else {
                //                post(action, 3000);
            }
        }
        if (intent.getCategories() != null) {
            for (String s : intent.getCategories()) {
                //                post(s, 2000);
            }
        }
    }

    @Override
    public void onCreate() {
        registerReceiver();
        setListeners();
        super.onCreate();
    }

    private void registerReceiver() {
        Log.d("registerReceiver()", "run");
        registerReceiver(mBroadcastReceiver, new IntentFilter(MY_ACTION_MAIN_ACTIVITY_EXIT));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_PACKAGE_ADDED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_PACKAGE_REPLACED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_PACKAGE_CHANGED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_PACKAGE_DATA_CLEARED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager
                .NETWORK_STATE_CHANGED_ACTION));


    }

    private void setListeners() {
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context
                .CLIPBOARD_SERVICE);
        Log.i("setListeners()", "a OnPrimaryClipChangedListener() was added!");
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager
                .OnPrimaryClipChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onPrimaryClipChanged() {
                Log.i("onPrimaryClipChanged()", "listen !");
                ClipData clip = clipboardManager.getPrimaryClip();
                if (clip != null) {
                    ClipData.Item item = clip.getItemAt(0);


                    if (item != null) {
                        CharSequence text = item.getText();
                        Uri uri = item.getUri();
                        //TODO need API 16
                        CharSequence htmlText = item.getHtmlText();
                        Log.i("onPrimaryClipChanged()", "clipdata text: " + text + " uri: " + uri
                                + " htmlText: " + htmlText + " intent: " + item.getIntent());
                        if (text != null) {
                            if (htmlText == null) {
                                new Note.Builder().setContent(text.toString()).create().commit
                                        (DatabaseHelper.getDatabaseHelper(MonitorService
                                                .this));
                                post(text.toString(), 3000, TEXT_WHITH_BUTTON_TOAST);
                            } else {
                                List<String> list = Collections.singletonList(uri.toString());
                                new Note.Builder()
                                        .setImagesPath(list)
                                        .create()
                                        .commit(DatabaseHelper.getDatabaseHelper
                                                (MonitorService.this));
                                post(text.toString(), 3000, PICTURE_WHITH_BUTTON_TOAST);
                            }

                        }
                    }
                }
            }
        });
    }

    public void post(String text, int time) {
        post(text, time, SIMPLE_TOAST);
    }

    public static final int SIMPLE_TOAST = 0;
    public static final int TEXT_WHITH_BUTTON_TOAST = 1;
    public static final int PICTURE_WHITH_BUTTON_TOAST = 2;

    public void post(String text, int time, int kind) {
        Toast.makeText(this, text, time).show();
        //        SuperToast superToast;
        //        switch (kind) {
        //            case TEXT_WHITH_BUTTON_TOAST:
        //                Toast toast = new Toast(this);
        //                LayoutInflater inflater = LayoutInflater.from(this);
        //                View view = inflater.inflate(R.layout.toast_with_button, null);
        //                ((TextView) view.findViewById(R.id.toast_text)).setText(text);
        //                view.findViewById(R.id.toast_button).setOnClickListener(new View
        // .OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //
        //
        //                    }
        //                });
        //                view.setFocusable(true);
        //                toast.setView(view);
        //                toast.setDuration(time);
        //                toast.show();
        //
        //
        ////
        ////                WindowManager windowManager = (WindowManager) getSystemService
        /// (WINDOW_SERVICE);
        ////                Point point = new Point();
        ////                windowManager.getDefaultDisplay().getSize(point);
        ////                WindowManager.LayoutParams params = new WindowManager.LayoutParams
        /// (LayoutParams.TYPE_PHONE, LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager
        /// .LayoutParams.TYPE_SYSTEM_ALERT);
        ////                params.width =  LayoutParams.WRAP_CONTENT;
        ////                params.height = LayoutParams.WRAP_CONTENT;
        ////                windowManager.addView(view, params);
        //
        //
        //                break;
        //            case PICTURE_WHITH_BUTTON_TOAST:
        //                break;
        //            default:
        //                superToast = SuperToast.create(this, text, time);
        //                superToast.setBackground(R.drawable.toast_background);
        //                superToast.setAnimations(SuperToast.Animations.POPUP);
        //                superToast.show();
        //        }


    }

    @Override public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );

                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity) { Log.i("CurrentActivity", componentName.flattenToShortString()); }
            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
        //Just in case this helps
        { config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS; }

        setServiceInfo(config);
    }

    @Override public void onInterrupt() {

    }
}
