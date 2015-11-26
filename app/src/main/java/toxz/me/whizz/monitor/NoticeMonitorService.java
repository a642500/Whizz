package toxz.me.whizz.monitor;

import android.app.Service;
import android.content.*;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import toxz.me.whizz.R;
import toxz.me.whizz.application.MyBinder;
import toxz.me.whizz.data.DatabaseHelper;
import toxz.me.whizz.data.Note;

/**
 * Created by Carlos on 4/16/2014.
 */


public class NoticeMonitorService extends Service {

    public static final String MY_ACTION_MAIN_ACTIVITY_EXIT = "com.unique.whizzdo.MainActivity.EXIT";


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
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_PACKAGE_CHANGED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_PACKAGE_DATA_CLEARED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));


    }

    private void setListeners() {
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        Log.i("setListeners()", "a OnPrimaryClipChangedListener() was added!");
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.i("onPrimaryClipChanged()", "listen !");
                ClipData clip = clipboardManager.getPrimaryClip();
                if (clip != null) {
                    ClipData.Item item = clip.getItemAt(0);


                    if (item != null) {
                        CharSequence text = item.getText();
                        Uri uri = item.getUri();
                        CharSequence htmlText = item.getHtmlText();
                        Log.i("onPrimaryClipChanged()", "clipdata text: " + text + " uri: " + uri + " htmlText: " + htmlText + " intent: " + item.getIntent());
                        if (text != null) {
                            if (htmlText == null) {
                                new Note.Builder().setContent(text.toString()).create().commit(DatabaseHelper.getDatabaseHelper(NoticeMonitorService.this));
                                post(text.toString(), 3000, TEXT_WHITH_BUTTON_TOAST);
                            } else {
                                ArrayList<Uri> arrayList = new ArrayList<Uri>();
                                arrayList.add(uri);
                                new Note.Builder().setImageUris(arrayList).create().commit(DatabaseHelper.getDatabaseHelper(NoticeMonitorService.this));
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
        Toast.makeText(this,text,time).show();
//        SuperToast superToast;
//        switch (kind) {
//            case TEXT_WHITH_BUTTON_TOAST:
//                Toast toast = new Toast(this);
//                LayoutInflater inflater = LayoutInflater.from(this);
//                View view = inflater.inflate(R.layout.toast_with_button, null);
//                ((TextView) view.findViewById(R.id.toast_text)).setText(text);
//                view.findViewById(R.id.toast_button).setOnClickListener(new View.OnClickListener() {
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
////                WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
////                Point point = new Point();
////                windowManager.getDefaultDisplay().getSize(point);
////                WindowManager.LayoutParams params = new WindowManager.LayoutParams(LayoutParams.TYPE_PHONE, LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
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

    public IBinder onBind(Intent intent) {
        Log.i("NoticeMonitorService", "Service bind");
        return new MyBinder(this);
    }


}
