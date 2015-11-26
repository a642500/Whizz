package toxz.me.whizz.application;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.util.Locale;

import toxz.me.whizz.MainActivity;
import toxz.me.whizz.R;

/**
 * Created by carlos on 5/31/14.
 */
public class ShortCutCreator {

    public static final int NEW_TEXT_NOTE_SHORTCUT = 0;


    public static final String EXTRA_LAUNCH_METHOD = "from shortcut";


    public static void addShortcut(Context context, int shortCutKind) {
        Log.i("addShortcut()", "shortCut " + shortCutKind + " created !");
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        Intent shortcutIntent;

        switch (shortCutKind) {
            case NEW_TEXT_NOTE_SHORTCUT:
                shortcutIntent = new Intent(context, MainActivity.class);
                shortcutIntent.putExtra(EXTRA_LAUNCH_METHOD, NEW_TEXT_NOTE_SHORTCUT);
                shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//TODO BUG:启动多个任务，而且，从快捷方式启动的无法刷新界面
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.short_cut_name_new_text_note));
                shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.icon_short_cut_new_text_note));
                break;
            default:
                return;
        }
        shortcut.putExtra("duplicate", false); //不允许重复创建
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        context.sendBroadcast(shortcut);
    }

}
