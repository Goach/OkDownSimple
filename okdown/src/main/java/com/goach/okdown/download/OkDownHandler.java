package com.goach.okdown.download;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.goach.okdown.callback.OkDownListener;
import com.goach.okdown.data.OkDownConst;
import com.goach.okdown.db.Db;
import com.goach.okdown.model.OkDownInfo;
import com.goach.okdown.utils.Utils;
import java.io.File;
import static com.goach.okdown.data.OkDownConst.CANCEL;
import static com.goach.okdown.data.OkDownConst.DESTROY;
import static com.goach.okdown.data.OkDownConst.DOWNLOADING;
import static com.goach.okdown.data.OkDownConst.ERROR;
import static com.goach.okdown.data.OkDownConst.FINISH;
import static com.goach.okdown.data.OkDownConst.PAUSE;
import static com.goach.okdown.data.OkDownConst.START;

/**
 * author: Goach.zhong
 * Date: 2018/11/27 10:04.
 * Des:
 */
public class OkDownHandler {
    private String url;
    private String path;
    private String name;
    private int mChildTaskCount;

    private Context mCtx;
    private OkDownListener mOkDownListener;
    private OkDownInfo mOkDownInfo;
    private FileTask mFileTask;
    private int mCurrentStatus = OkDownConst.NONE;
    private boolean isSupportRange;
    private boolean isNeedReset;
    private int mCurrentLength;
    private int mTotalLength;
    private int mTempChildTaskCount;//暂停线程数
    private long lastProgressTime;
    private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int mLastStatus = mCurrentStatus;
            mCurrentStatus = message.what;
            mOkDownInfo.setStatus(mCurrentStatus);
            switch (mCurrentStatus){
                case START:{
                    Bundle bundleParams = message.getData();
                    mTotalLength = bundleParams.getInt("totalLength");
                    mCurrentLength = bundleParams.getInt("currentLength");
                    String lastModify = bundleParams.getString("lastModify");
                    isSupportRange = bundleParams.getBoolean("isSupportRange");
                    mOkDownInfo.setTotalLength(mTotalLength);
                    mOkDownInfo.setCurrentLength(mCurrentLength);
                    mOkDownInfo.setDate(System.currentTimeMillis());
                    mOkDownInfo.setLastModify(lastModify);
                    mOkDownInfo.setProgress(0f);
                    if(!isSupportRange){
                        mChildTaskCount = 1;
                    }else if(mCurrentLength == 0){
                        Db.getInstance(mCtx).insertData(mOkDownInfo);
                    }
                    mOkDownInfo.setChildTaskCount(mChildTaskCount);
                    if(mOkDownListener != null){
                        mOkDownListener.onStart(mOkDownInfo);
                    }
                    break;
                }
                case DOWNLOADING:{
                    synchronized (this){
                        mCurrentLength += message.arg1;
                        mOkDownInfo.setProgress(Utils.getProgress(mCurrentLength,mTotalLength));
                        if(mOkDownListener!=null&&(System.currentTimeMillis() - lastProgressTime >= 20 ||
                                mCurrentLength == mTotalLength)){
                            mOkDownInfo.setCurrentLength(mCurrentLength);
                            mOkDownInfo.setTotalLength(mTotalLength);
                            mOkDownInfo.setProgress(Utils.getProgress(mCurrentLength,mTotalLength));
                            mOkDownListener.onProgress(mOkDownInfo.getProgress());
                            lastProgressTime = System.currentTimeMillis();
                        }
                        if (mCurrentLength >= mTotalLength) {
                            mHandler.sendEmptyMessage(FINISH);
                        }
                    }
                    break;
                }
                case CANCEL:{
                    synchronized (this){
                        mTempChildTaskCount ++;
                        if(mTempChildTaskCount == mChildTaskCount || mLastStatus == FINISH || mLastStatus == ERROR){
                            mTempChildTaskCount = 0;
                            if (mOkDownListener != null) {
                                mOkDownInfo.setCurrentLength(0);
                                mOkDownInfo.setTotalLength(mTotalLength);
                                mOkDownInfo.setProgress(0);
                                mOkDownListener.onProgress(0);
                                mOkDownListener.onCancel(mOkDownInfo);
                            }
                            mCurrentLength = 0;
                            if (isSupportRange) {
                                Db.getInstance(mCtx).deleteData(url);
                                Utils.deleteFile(new File(path, name + ".temp"));
                            }
                            if (isNeedReset) {
                                isNeedReset = false;
                                //TODO 请求重新设置
                                //OkDownManager.getInstance(context).innerRestart(url);
                            }
                        }
                    }
                    break;
                }
                case PAUSE:{
                    synchronized (this){
                        if (isSupportRange) {
                            Db.getInstance(mCtx).updateProgress(mCurrentLength, Utils.getProgress(mCurrentLength, mTotalLength), PAUSE, url);
                        }
                        mTempChildTaskCount++;
                        if (mTempChildTaskCount == mChildTaskCount) {
                            if (mOkDownListener != null) {
                                mOkDownListener.onPause(mOkDownInfo);
                            }
                            mTempChildTaskCount = 0;
                        }
                    }
                    break;
                }
                case FINISH:{
                    if (isSupportRange) {
                        Utils.deleteFile(new File(path, name + ".temp"));
                        Db.getInstance(mCtx).deleteData(url);
                    }
                    if (mOkDownListener != null) {
                        mOkDownListener.onFinish(mOkDownInfo);
                    }
                    break;
                }
                case DESTROY:{
                    synchronized (this) {
                        if (isSupportRange) {
                            Db.getInstance(mCtx).updateProgress(mCurrentLength, Utils.getProgress(mCurrentLength, mTotalLength), DESTROY, url);
                        }
                    }
                    break;
                }
                case OkDownConst.ERROR:{
                    if (isSupportRange) {
                        Db.getInstance(mCtx).updateProgress(mCurrentLength, Utils.getProgress(mCurrentLength, mTotalLength), ERROR, url);
                    }
                    if (mOkDownListener != null) {
                        mOkDownListener.onError(new Exception((String) message.obj));
                    }
                    break;
                }
            }
            return false;
        }
    });

    public OkDownHandler(Context context, OkDownInfo downloadData, OkDownListener OkDownInfo) {
        this.mCtx = context;
        this.mOkDownListener = OkDownInfo;

        this.url = downloadData.getUrl();
        this.path = downloadData.getPath();
        this.name = downloadData.getName();
        this.mChildTaskCount = downloadData.getChildTaskCount();

        OkDownInfo dbData = Db.getInstance(context).getData(url);
        this.mOkDownInfo = dbData == null ? downloadData : dbData;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public int getCurrentState() {
        return mCurrentStatus;
    }

    public OkDownInfo getDownloadData() {
        return mOkDownInfo;
    }

    public void setFileTask(FileTask fileTask) {
        this.mFileTask = fileTask;
    }

    /**
     * 下载中退出时保存数据、释放资源
     */
    public void destroy() {
        if (mCurrentStatus == CANCEL || mCurrentStatus == PAUSE) {
            return;
        }
        mFileTask.destroy();
    }

    /**
     * 暂停（正在下载才可以暂停）
     * 如果文件不支持断点续传则不能进行暂停操作
     */
    public void pause() {
        if (mCurrentStatus == DOWNLOADING) {
            mFileTask.pause();
        }
    }

    /**
     * 取消（已经被取消、下载结束则不可取消）
     */
    public void cancel(boolean isNeedRestart) {
        this.isNeedReset = isNeedRestart;
        if (mCurrentStatus == DOWNLOADING) {
            mFileTask.cancel();
        } else if (mCurrentStatus == PAUSE || mCurrentStatus == ERROR) {
            mHandler.sendEmptyMessage(CANCEL);
        }
    }


}
