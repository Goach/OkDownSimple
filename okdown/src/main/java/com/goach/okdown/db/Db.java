package com.goach.okdown.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.goach.okdown.model.OkDownInfo;

import java.util.ArrayList;
import java.util.List;

import static com.goach.okdown.data.OkDownConst.DOWNLOADING;
import static com.goach.okdown.db.DbOpenHelper.TABLE_NAME_DOWNLOAD;

/**
 * author: Goach.zhong
 * Date: 2018/11/27 13:48.
 * Des:
 */
public class Db {
    private static volatile Db mInstance;
    private SQLiteDatabase mSqlDb;
    private Db(Context ctx){
        DbOpenHelper dbOpenHelper = new DbOpenHelper(ctx);
        mSqlDb = dbOpenHelper.getWritableDatabase();
    }
    public static Db getInstance(Context ctx){
        if(null == mInstance){
            synchronized (Db.class){
                if(null == mInstance){
                    mInstance = new Db(ctx);
                }
            }
        }
        return mInstance;
    }
    /**
     * 插入数据
     */
    public void insertData(OkDownInfo info){
        ContentValues insertData = new ContentValues();
        insertData.put("url",info.getUrl());
        insertData.put("path",info.getPath());
        insertData.put("name",info.getName());
        insertData.put("child_task_count",info.getChildTaskCount());
        insertData.put("current_length",info.getCurrentLength());
        insertData.put("total_length",info.getTotalLength());
        insertData.put("progress",info.getProgress());
        insertData.put("status",info.getStatus());
        insertData.put("last_modify",info.getLastModify());
        insertData.put("date",info.getDate());
        mSqlDb.insert(TABLE_NAME_DOWNLOAD,null,insertData);
    }
    public void insertDatas(List<OkDownInfo> infos) {
        for (OkDownInfo info : infos) {
            insertData(info);
        }
    }
    /**
     * 更新下载信息
     */
    public void updateProgress(int currentSize, float progress, int status, String url) {
        ContentValues values = new ContentValues();
        if (status != DOWNLOADING){
            values.put("current_length", currentSize);
            values.put("progress", progress);
        }
        values.put("status", status);
        mSqlDb.update(TABLE_NAME_DOWNLOAD, values, "url = ?", new String[]{url});
    }

    /**
     * 删除下载信息
     */
    public void deleteData(String url) {
        mSqlDb.delete(TABLE_NAME_DOWNLOAD, "url = ?", new String[]{url});
    }

    /**
     * 获得url对应的下载数据
     */
    public OkDownInfo getData(String url) {
        Cursor cursor = mSqlDb.query(TABLE_NAME_DOWNLOAD, null, "url = ?",
                new String[]{url}, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
        OkDownInfo data = new OkDownInfo();
        data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        data.setPath(cursor.getString(cursor.getColumnIndex("path")));
        data.setName(cursor.getString(cursor.getColumnIndex("name")));
        data.setChildTaskCount(cursor.getInt(cursor.getColumnIndex("child_task_count")));
        data.setCurrentLength(cursor.getInt(cursor.getColumnIndex("current_length")));
        data.setTotalLength(cursor.getInt(cursor.getColumnIndex("total_length")));
        data.setProgress(cursor.getFloat(cursor.getColumnIndex("progress")));
        data.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        data.setLastModify(cursor.getString(cursor.getColumnIndex("last_modify")));
        data.setDate(cursor.getInt(cursor.getColumnIndex("date")));
        cursor.close();
        return data;
    }

    /**
     * 获得全部下载数据
     *
     * @return
     */
    public List<OkDownInfo> getAllData() {
        List<OkDownInfo> list = new ArrayList<>();
        Cursor cursor = mSqlDb.query(TABLE_NAME_DOWNLOAD, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                OkDownInfo data = new OkDownInfo();
                data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                data.setPath(cursor.getString(cursor.getColumnIndex("path")));
                data.setName(cursor.getString(cursor.getColumnIndex("name")));
                data.setChildTaskCount(cursor.getInt(cursor.getColumnIndex("child_task_count")));
                data.setCurrentLength(cursor.getInt(cursor.getColumnIndex("current_length")));
                data.setTotalLength(cursor.getInt(cursor.getColumnIndex("total_length")));
                data.setProgress(cursor.getFloat(cursor.getColumnIndex("progress")));
                data.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                data.setLastModify(cursor.getString(cursor.getColumnIndex("last_modify")));
                data.setDate(cursor.getInt(cursor.getColumnIndex("date")));
                list.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
