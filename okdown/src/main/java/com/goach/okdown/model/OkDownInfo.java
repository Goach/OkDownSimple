package com.goach.okdown.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.goach.okdown.data.OkDownConst;

/**
 * @author Goach
 * Data: 2018/11/26
 * des: 下载记录的一些信息
 **/
public class OkDownInfo implements Parcelable{
    private String url;
    private String path;
    private String name;
    private int currentLength;
    private int totalLength;
    private int childTaskCount;
    private long date;
    private String lastModify;
    private float progress;
    private int status = OkDownConst.NONE;

    public OkDownInfo() {
    }

    protected OkDownInfo(Parcel in) {
        url = in.readString();
        path = in.readString();
        name = in.readString();
        currentLength = in.readInt();
        totalLength = in.readInt();
        childTaskCount = in.readInt();
        date = in.readLong();
        lastModify = in.readString();
        progress = in.readFloat();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(int currentLength) {
        this.currentLength = currentLength;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public int getChildTaskCount() {
        return childTaskCount;
    }

    public void setChildTaskCount(int childTaskCount) {
        this.childTaskCount = childTaskCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public static Creator<OkDownInfo> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<OkDownInfo> CREATOR = new Creator<OkDownInfo>() {
        @Override
        public OkDownInfo createFromParcel(Parcel in) {
            return new OkDownInfo(in);
        }

        @Override
        public OkDownInfo[] newArray(int size) {
            return new OkDownInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int in) {
       parcel.writeString(url);
       parcel.writeString(path);
       parcel.writeString(name);
       parcel.writeInt(currentLength);
       parcel.writeInt(totalLength);
       parcel.writeInt(childTaskCount);
       parcel.writeLong(date);
       parcel.writeString(lastModify);
       parcel.writeInt(status);
       parcel.writeFloat(progress);
    }
}
