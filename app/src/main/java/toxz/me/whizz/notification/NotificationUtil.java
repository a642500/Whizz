package toxz.me.whizz.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import toxz.me.whizz.MainActivity;
import toxz.me.whizz.R;
import toxz.me.whizz.data.DatabaseHelper;
import toxz.me.whizz.data.MySQLiteOpenHelper;
import toxz.me.whizz.data.Note;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Carlos on 11/27/16.
 */

public class NotificationUtil {
    public static final int ID_STICK_NOTIFICATION = 1;

    public static void registerOrUpdateNotificationBar(final Context context) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override public void run() {
                List<Note> notes = DatabaseHelper.getDatabaseHelper(context).getNotes
                        (false, MySQLiteOpenHelper.COLUMN_CREATED_TIME, DatabaseHelper.DESC);
                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(Note lhs, Note rhs) {
                        int a = lhs.getImportance() == -1 ? 2 : lhs.getImportance();
                        int b = rhs.getImportance() == -1 ? 2 : rhs.getImportance();
                        return a - b;
                    }
                });
                NotificationUtil.registerNotificationBar(context, notes);
            }
        });
    }

    private static void registerNotificationBar(Context context, List<Note> notes) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (NOTIFICATION_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(context.getApplicationContext(), 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT));

        if (notes == null || notes.isEmpty()) {
            builder.setContentTitle("最近没有任务");
        } else {
            builder.setContentTitle(String.format(Locale.getDefault(), "近期有%d项任务", notes.size()));

            Note firstNode = notes.get(0);
            if (!firstNode.getImagesPath().isEmpty()) {
                // first has picture
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(context).load("file://"
                            + firstNode.getImagesPath().get(0)).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat
                        .BigPictureStyle().bigPicture(bitmap);
                if (!TextUtils.isEmpty(firstNode.getContent())) {
                    builder.setContentText(firstNode.getContent());
                }
                builder.setStyle(bigPictureStyle);
            } else {
                List<String> noteStrings = new ArrayList<>(notes.size());
                for (Note note : notes) {
                    if (!TextUtils.isEmpty(note.getContent())) {
                        noteStrings.add(note.getContent());
                    }
                }

                if (noteStrings.size() == 0) {
                    builder.setContentText("无");
                } else {
                    builder.setContentText(noteStrings.get(0));

                    if (noteStrings.size() >= 1) {
                        builder.setContentText(noteStrings.get(0));
                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle
                                ().setSummaryText("近期任务");
                        for (String s : noteStrings) {
                            inboxStyle.addLine(s);
                        }
                        builder.setStyle(inboxStyle);
                    }
                }
            }
        }

        notificationManager.notify(ID_STICK_NOTIFICATION, builder.build());
    }
}
