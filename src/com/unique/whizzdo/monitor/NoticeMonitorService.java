package com.unique.whizzdo.monitor;

import android.app.ActionBar;
import android.app.Service;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.unique.whizzdo.R;
import com.unique.whizzdo.application.MyBinder;

import java.sql.Time;
import java.util.Iterator;
import java.util.Set;

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
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_MAIN));
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
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clip = clipboardManager.getPrimaryClip();
//                if (clip != null) {
//
//                    String text = null;
//                    String title = null;
//
//                    // Gets the first item from the clipboard data
//                    ClipData.Item item = clip.getItemAt(0);
//
//                    // Tries to get the item's contents as a URI pointing to a note
//                    Uri uri = item.getUri();
//
//                    // Tests to see that the item actually is an URI, and that the URI
//                    // is a content URI pointing to a provider whose MIME type is the same
//                    // as the MIME type supported by the Note pad provider.
//                    if (uri != null && NotePad.Notes.CONTENT_ITEM_TYPE.equals(cr.getType(uri))) {
//                        // The clipboard holds a reference to data with a note MIME type. This copies it.
//                        Cursor orig = cr.query(
//                                uri,            // URI for the content provider
//                                PROJECTION,     // Get the columns referred to in the projection
//                                null,           // No selection variables
//                                null,           // No selection variables, so no criteria are needed
//                                null            // Use the default sort order
//                        );
//                        // If the Cursor is not null, and it contains at least one record
//                        // (moveToFirst() returns true), then this gets the note data from it.
//                        if (orig != null) {
//                            if (orig.moveToFirst()) {
//                                int colNoteIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_NOTE);
//                                int colTitleIndex = mCursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE);
//                                text = orig.getString(colNoteIndex);
//                                title = orig.getString(colTitleIndex);
//                            }
//                            // Closes the cursor.
//                            orig.close();
//                        }
//                    }
//                    // If the contents of the clipboard wasn't a reference to a note, then
//                    // this converts whatever it is to text.
//                    if (text == null) {
//                        text = item.coerceToText(this).toString();
//                    }
//                    // Updates the current note with the retrieved title and text.
////                }
            }
        });
    }

    public void post(String text, int time) {
        SuperToast toast = SuperToast.create(this, text, time);
        toast.setBackground(R.drawable.toast_background);
        toast.setAnimations(SuperToast.Animations.POPUP);
        toast.show();
    }

    public void postSpecial(String text, int time) {
        Toast toast = new Toast(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.toast_with_button, null);
        ((TextView) view.findViewById(R.id.toast_text)).setText(text);
        view.findViewById(R.id.toast_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        toast.setView(view);
        toast.setDuration(time);
        toast.show();
    }

    public IBinder onBind(Intent intent) {
        Log.i("NoticeMonitorService", "Service bind");
        return new MyBinder(this);
    }


}
