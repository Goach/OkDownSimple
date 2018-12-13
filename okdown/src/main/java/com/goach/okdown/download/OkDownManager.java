package com.goach.okdown.download;

import android.content.Context;
import com.goach.okdown.callback.OkDownListener;
import com.goach.okdown.model.OkDownInfo;
import java.util.HashMap;
import java.util.Map;
import static android.drm.DrmStore.Playback.PAUSE;
import static com.goach.okdown.data.OkDownConst.ERROR;
import static com.goach.okdown.data.OkDownConst.FINISH;
import static com.goach.okdown.data.OkDownConst.NONE;

/**
 * author: Goach.zhong
 * Date: 2018/11/28 14:45.
 * Des:
 */
public class OkDownManager {

    private Map<String, OkDownHandler> progressHandlerMap = new HashMap<>();//保存任务的进度处理对象
    private Map<String, OkDownInfo> downloadDataMap = new HashMap<>();//保存任务数据
    private Map<String, OkDownListener> callbackMap = new HashMap<>();//保存任务回调
    private Map<String, FileTask> fileTaskMap = new HashMap<>();//保存下载线程

    private static volatile OkDownManager OkDownManager;
    private OkDownInfo downloadData;

    public static OkDownManager getInstance(){
        if(OkDownManager == null){
            synchronized (OkDownManager.class){
                if(OkDownManager == null){
                    OkDownManager = new OkDownManager();
                }
            }
        }
        return OkDownManager;
    }

    private OkDownManager(){}

    public synchronized void init(String url,String path,String name,int childTaskCount){
        downloadData = new OkDownInfo();
        downloadData.setUrl(url);
        downloadData.setPath(path);
        downloadData.setName(name);
        downloadData.setChildTaskCount(childTaskCount);
    }


    /**
     * 链式开启下载
     *
     * @param okDownListener
     * @return
     */
    public OkDownManager start(Context ctx,OkDownListener okDownListener) {
        execute(ctx,downloadData, okDownListener);
        return OkDownManager;
    }

    /****
     * 根据URL开始下载
     * @param downloadData
     * @param okDownListener
     * @return
     */
    public OkDownManager start(Context ctx,OkDownInfo downloadData,OkDownListener okDownListener){
        execute(ctx,downloadData,okDownListener);
        return OkDownManager;
    }

    /**
     * 根据url开始下载（需先注册监听）
     *
     * @param url
     */
    public OkDownManager start(Context ctx,String url) {
        execute(ctx,downloadDataMap.get(url), callbackMap.get(url));
        return OkDownManager;
    }

    /**
     * 暂停
     *
     * @param url
     */
    public void pause(String url) {
        if (progressHandlerMap.containsKey(url))
            progressHandlerMap.get(url).pause();
    }

    /**
     * 继续
     *
     * @param url
     */
    public void resume(Context ctx,String url) {
        if (progressHandlerMap.containsKey(url) &&
                (progressHandlerMap.get(url).getCurrentState() == PAUSE ||
                        progressHandlerMap.get(url).getCurrentState() == ERROR)) {
            progressHandlerMap.remove(url);
            execute(ctx,downloadDataMap.get(url), callbackMap.get(url));
        }
    }


    /**
     * 重新开始
     *
     * @param url
     */
    public void restart(Context ctx,String url) {
        //文件已下载完成的情况
        if (progressHandlerMap.containsKey(url) && progressHandlerMap.get(url).getCurrentState() == FINISH) {
            progressHandlerMap.remove(url);
            fileTaskMap.remove(url);
            innerRestart(ctx,url);
            return;
        }

        //任务已经取消，则直接重新下载
        if (!progressHandlerMap.containsKey(url)) {
            innerRestart(ctx,url);
        } else {
            innerCancel(url, true);
        }
    }

    /**
     * 退出时释放资源
     *
     * @param url
     */
    public void destroy(String url) {
        if (progressHandlerMap.containsKey(url)) {
            progressHandlerMap.get(url).destroy();
            progressHandlerMap.remove(url);
            callbackMap.remove(url);
            downloadDataMap.remove(url);
            fileTaskMap.remove(url);
        }
    }

    public void destroy(String... urls) {
        if (urls != null) {
            for (String url : urls) {
                destroy(url);
            }
        }
    }

    /***
     * 下载
     * @param downloadData
     * @param okDownListener
     */
    private synchronized void execute(Context ctx,OkDownInfo downloadData,OkDownListener okDownListener){
        if(progressHandlerMap.get(downloadData.getUrl()) != null){
            return;
        }

        if(downloadData.getChildTaskCount() == 0){
            downloadData.setChildTaskCount(1);
        }

        OkDownHandler progressHandler = new OkDownHandler(ctx, downloadData, okDownListener);
        FileTask fileTask = new FileTask(ctx, downloadData, progressHandler.getHandler());
        progressHandler.setFileTask(fileTask);

        downloadDataMap.put(downloadData.getUrl(),downloadData);
        callbackMap.put(downloadData.getUrl(),okDownListener);
        fileTaskMap.put(downloadData.getUrl(),fileTask);
        progressHandlerMap.put(downloadData.getUrl(),progressHandler);

        ThreadPool.getInstance().getThreadPoolExecutor().execute(fileTask);

        if(ThreadPool.getInstance().getThreadPoolExecutor().getActiveCount() == ThreadPool.getInstance().getCorePoolSize()){
            okDownListener.onWait(downloadDataMap.get(downloadData.getUrl()));
        }

    }

    /**
     * 实际的重新下载操作
     *
     * @param url
     */
    protected void innerRestart(Context ctx,String url) {
        execute(ctx,downloadDataMap.get(url), callbackMap.get(url));
    }

    /**
     * 取消
     *
     * @param url
     */
    public void cancel(String url) {
        innerCancel(url, false);
    }

    public void innerCancel(String url, boolean isNeedRestart) {
        if (progressHandlerMap.get(url) != null) {
            if (progressHandlerMap.get(url).getCurrentState() == NONE) {
                //取消缓存队列中等待下载的任务
                ThreadPool.getInstance().getThreadPoolExecutor().remove(fileTaskMap.get(url));
                callbackMap.get(url).onCancel(downloadDataMap.get(url));
            } else {
                //取消已经开始下载的任务
                progressHandlerMap.get(url).cancel(isNeedRestart);
            }
            progressHandlerMap.remove(url);
            fileTaskMap.remove(url);
        }
    }


    /**
     * 注册监听
     *
     * @param downloadData
     * @param okDownListener
     */
    public synchronized void setOnOkDownInfo(OkDownInfo downloadData, OkDownListener okDownListener) {
        downloadDataMap.put(downloadData.getUrl(), downloadData);
        callbackMap.put(downloadData.getUrl(), okDownListener);
    }


}
