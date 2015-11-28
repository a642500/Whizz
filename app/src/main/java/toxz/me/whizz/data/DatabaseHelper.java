package toxz.me.whizz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Carlos on 4/16/2014.
 */


public class DatabaseHelper {
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";
    private static DatabaseHelper mInstance;
    private ArrayList<DataChangedListener> mListeners;
    private MySQLiteOpenHelper mHelper;

    //隐藏的构造器
    private DatabaseHelper(Context context) {
        mHelper = new MySQLiteOpenHelper(context);
    }

    public static DatabaseHelper getDatabaseHelper(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    private void notifyDataChanged() {
        Log.i("notifyDataChanged()", "Data changed ! Listener numbers is " + (mListeners != null ? mListeners.size() : 0));
        if (mListeners != null) {
            for (int i = 0; i < mListeners.size(); i++) {
                mListeners.get(i).notifyDataChanged();
            }
        }

    }

    public void setDatabaseChangedListener(DataChangedListener listener) {
        if (listener != null && mListeners == null) {
            mListeners = new ArrayList<DataChangedListener>();
            mListeners.add(listener);
        }
    }

    private void DatabaseClose(SQLiteDatabase database) {
        if (database != null && database.isOpen()) {
            database.close();
        }
        notifyDataChanged();
    }

    /**
     * to save a new note or a changed note.
     */
    protected void saveNote(Note note) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        try {
            assert database != null;
            ContentValues values = new ContentValues();
            values.put(MySQLiteOpenHelper.COLUMN_CONTENT, note.getContent());
            values.put(MySQLiteOpenHelper.COLUMN_CREATED_TIME, note.getCreatedTime());
            values.put(MySQLiteOpenHelper.COLUMN_DEADLINE, note.getDeadline());
            values.put(MySQLiteOpenHelper.COLUMN_FINISHED_TIME, note.getFinishedTime());
            values.put(MySQLiteOpenHelper.COLUMN_IS_FINISHED, String.valueOf(note.isFinished() ? 1 : 0));
            values.put(MySQLiteOpenHelper.COLUMN_IS_NOTICE, String.valueOf(note.isNotice() ? 1 : 0));
            values.put(MySQLiteOpenHelper.COLUMN_IMPORTANCE, note.getImportance());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); //构造一个字节输出流
            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream); //构造一个类输出流
            Log.i("saveNotes()", "uri list" + Arrays.toString(note.getImagesUris().toArray()));
            oos.writeObject(toStringList(note.getImagesUris())); //写这个对象
            Log.i("saveNotes()", "string list" + Arrays.toString(toStringList(note.getImagesUris()).toArray()));

            byte[] buf = byteArrayOutputStream.toByteArray(); //从这个地层字节流中把传输的数组给一个新的数组
            oos.flush();

            values.put(MySQLiteOpenHelper.COLUMN_IMAGES_URIS, buf);

            if (note.getID() == -1) {
                database.insert(MySQLiteOpenHelper.TABLE_NAME_NOTES, null, values);
                Log.i("saveNote()", "new note !");
            } else {
                Log.i("saveNote()", "update note !");
                Log.i("saveNote()", "ID is " + note.getID());
                database.update(MySQLiteOpenHelper.TABLE_NAME_NOTES, values, MySQLiteOpenHelper.COLUMN_ID + " = ? ",
                        new String[]{String.valueOf(note.getID())});
            }
            Log.i("saveNote()", note.getContent() + ",created at " + note.getCreatedTime() +
                    ",deadline is " + note.getDeadline() + " , isNotice is " + note.isNotice() + " , isFinished is " +
                    (note.isFinished() ? 1 : 0) + " , importance level is " + note.getImportance());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DatabaseClose(database);
        }
    }

    private List<String> toStringList(List<Uri> uriList) {
        List<String> list = new ArrayList<String>();
        for (Uri uri : uriList) {
            list.add(uri.toString());
        }
        return list;
    }

    private List<Uri> toUriList(List<String> stringList) {
        List<Uri> list = new ArrayList<Uri>();
        for (String s : stringList) {
            list.add(Uri.parse(s));
        }
        return list;
    }

    /**
     * @param isFinished that you need to query.
     * @param OrdBy      the targeted column. Such as MySQLiteOpenHelper.COLUMN_CREATED_TIME
     * @param DescOrAsc
     * @return the notes that unfinished.
     */
    public ArrayList<Note> getNotes(boolean isFinished, String OrdBy, String DescOrAsc) {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Note> notes = new ArrayList<Note>();
        try {
            if (database != null) {
                cursor = database.query(true, MySQLiteOpenHelper.TABLE_NAME_NOTES, new String[]{MySQLiteOpenHelper.COLUMN_ID, MySQLiteOpenHelper.COLUMN_CONTENT,
                                MySQLiteOpenHelper.COLUMN_CREATED_TIME, MySQLiteOpenHelper.COLUMN_IS_FINISHED, MySQLiteOpenHelper.COLUMN_FINISHED_TIME,
                                MySQLiteOpenHelper.COLUMN_DEADLINE, MySQLiteOpenHelper.COLUMN_IS_NOTICE, MySQLiteOpenHelper.COLUMN_IMPORTANCE, MySQLiteOpenHelper.COLUMN_IMAGES_URIS},
                        MySQLiteOpenHelper.COLUMN_IS_FINISHED + " = ? ", new String[]{String.valueOf(isFinished ? 1 : 0)}, null, null, OrdBy, null
                );//TODO 降序和升序
                while (cursor.moveToNext()) {
                    byte[] listsByte = cursor.getBlob(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_IMAGES_URIS));
                    ByteArrayInputStream bais = new ByteArrayInputStream(listsByte);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    ArrayList<String> arrayList = (ArrayList<String>) ois.readObject();

                    Note note = new Note.Builder()
                            .setID(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_ID)))
                            .setContent(cursor.getString(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_CONTENT)))
                            .setCreatedTime(cursor.getLong(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_CREATED_TIME)))
                            .setFinished(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_IS_FINISHED)) == 1)
                            .setFinishedTime(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_FINISHED_TIME)))
                            .setDeadline(cursor.getLong(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_DEADLINE)))
                            .setNotice(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_IS_NOTICE)) == 1)
                            .setImportance(cursor.getInt(cursor.getColumnIndex(MySQLiteOpenHelper.COLUMN_IMPORTANCE)))
                            .setImageUris(toUriList(arrayList))
                            .create();
                    notes.add(note);

                    Log.v("getNotes()", "note  is " + note.getContent() + ",created at " + note.getCreatedTime() +
                            ",deadline is " + note.getDeadline() + " , isNotice is " + note.isNotice() + " , isFinished is " +
                            note.isFinished() + " , importance level is " + note.getImportance() + " , ID is " + note.getID());
                    Log.v("getNotes()", "images string list: " + Arrays.toString(arrayList.toArray()));
                    Log.v("getNotes()", "images uri list: " + Arrays.toString(note.getImagesUris().toArray()));
                }
            }
            Log.v("vital", "getNotes() " + isFinished + " : return " + notes.size() + " notes");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (database != null) {
                database.close();
            }
        }
        return notes;
    }

    /**
     * @return true if there is any unfinished notes.
     */

    public boolean hasNotes() {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        assert database != null;
        Cursor cursor = database.query(false, MySQLiteOpenHelper.TABLE_NAME_NOTES, new String[]{MySQLiteOpenHelper.COLUMN_ID},
                MySQLiteOpenHelper.COLUMN_IS_FINISHED + " = ? ", new String[]{String.valueOf(0)}, null, null, null, null);//TODO
        Log.i("vital", "hasNotes() : cursor count = " + cursor.getCount());
        boolean result = cursor.getCount() > 0;
        cursor.close();
        database.close();
        return result;
    }

    protected void deleteNote(Note note) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        if (database != null) {
            Log.i("deleteNote()", "note ID is " + note.getID());
            database.beginTransaction();
            try {
                database.delete(MySQLiteOpenHelper.TABLE_NAME_NOTES, MySQLiteOpenHelper.COLUMN_ID + " = ? ", new String[]{String.valueOf(note.getID())});
                database.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
                DatabaseClose(database);
            }
        }
    }

}
