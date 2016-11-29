package toxz.me.whizz.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);


        NotificationCompat.Builder builder = (NotificationCompat.Builder)
                new android.support.v7.app.NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setContentIntent(PendingIntent.getActivity(context.getApplicationContext
                                        (), 0,
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

    public static void toastNote(final Context context, final Note note) {
        if (TextUtils.isEmpty(note.getContent())) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        Toast toast = new Toast(context);
        View root = inflater.inflate(R.layout.layout_toast_note, null);
        ((TextView) root.findViewById(R.id.toastText)).setText(note.getContent());
        toast.setView(root);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static void notifyNote(final Context context, final Note note) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);

        new NotificationCompat.Builder(context).setContentText(note.getContent()).setVisibility
                (NotificationCompat.VISIBILITY_PUBLIC).setPriority(android.support.v7.app
                .NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_REMINDER).setStyle(new
                NotificationCompat.InboxStyle()).build();
    }

    public static void dialogNote(final Context context, final Note note) {
        if (TextUtils.isEmpty(note.getContent())) {
            return;
        }

        final WindowManager service = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);

        final View root = View.inflate(context, R.layout.layout_dialog_note, null);
        ((TextView) root.findViewById(R.id.contentText)).setText(note.getContent());
        TextView dateText = (TextView) root.findViewById(R.id.dateText);
        long deadline = note.getDeadline();
        if (deadline != 0) {
            String date = new SimpleDateFormat("MM月dd日", Locale.getDefault())
                    .format(new Date(deadline));
            dateText.setText(date);
        } else {
            dateText.setVisibility(View.INVISIBLE);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.finishBtn:
                        note.setFinished(true);
                        note.setFinishedTime(System.currentTimeMillis());
                        note.commit(DatabaseHelper.getDatabaseHelper(context));
                        service.removeView(root);
                        break;
                    case R.id.delayBtn:
                        note.setDeadline(note.getDeadline() + 3600 * 24);
                        note.commit(DatabaseHelper.getDatabaseHelper(context));
                        service.removeView(root);
                        break;
                }
            }
        };
        root.findViewById(R.id.finishBtn).setOnClickListener(listener);
        root.findViewById(R.id.delayBtn).setOnClickListener(listener);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.setTitle("待办");

        service.addView(root, params);
    }
}
