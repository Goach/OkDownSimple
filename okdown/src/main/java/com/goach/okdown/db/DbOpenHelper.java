package com.goach.okdown.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * author: Goach.zhong
 * Date: 2018/11/27 13:36.
 * Des:
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    /**
     * 数据库名
     */
    private static final String DB_NAME = "okDown";

    /**
     * 数据库版本
     */
    private static final int VERSION = 1;

    public static String TABLE_NAME_DOWNLOAD = "ok_down_info";

    private static String CREATE_OK_DOWN_INFO_TABLE = "create table " + TABLE_NAME_DOWNLOAD + " ("
            + "id Integer primary key autoincrement,"
            + "url text,"
            + "path text,"
            + "name text,"
            + "child_task_count Integer,"
            + "current_length Integer,"
            + "total_length Integer,"
            + "progress Float,"
            + "status Integer,"
            + "last_modify text,"
            + "date text)";
    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_OK_DOWN_INFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
