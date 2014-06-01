package com.unique.whizzdo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.unique.whizzdo.application.MyApplication;

/**
 * 这个类会读取账户名并打开相应的数据库，初始化表
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME_NOTES = "notes";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_TIME = "time_created";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_IS_FINISHED = "is_finished";
    public static final String COLUMN_FINISHED_TIME = "time_finished";
    public static final String COLUMN_IS_NOTICE = "is_notice";
    public static final String COLUMN_ID = "notesId";
    public static final String COLUMN_IMPORTANCE = "importance";
    public static final String COLUMN_IMAGES_PATH = "image_paths";


    public MySQLiteOpenHelper(Context context) {
        super(context, getDatabaseName(context), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME_NOTES + " ( " + " \"notesId\"  INTEGER PRIMARY KEY AUTOINCREMENT  , " + COLUMN_CONTENT + " ,  "
                + COLUMN_CREATED_TIME + " , " + COLUMN_DEADLINE + " , " + COLUMN_IS_FINISHED + " , " + COLUMN_FINISHED_TIME
                + " , " + COLUMN_IS_NOTICE + " , " + COLUMN_IMPORTANCE + " , " + COLUMN_IMAGES_PATH + " ) ;";
        Log.i("vital", "create table sql: " + sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDatabaseName(Context context) {
        return MyApplication.AccountInfo.getAccount()+".db";
    }
}
