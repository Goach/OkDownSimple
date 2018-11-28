package com.goach.okdown.callback;

import com.goach.okdown.model.OkDownInfo;

/**
 * @author Goach
 * Date: 2018/11/26
 * des 回调接口
 **/
public interface OkDownListener {
    void onStart(OkDownInfo info);
    void onProgress(float progress);
    void onError(Exception e);
    void onPause(OkDownInfo info);
    void onCancel(OkDownInfo info);
    void onWait(OkDownInfo info);
    void onFinish(OkDownInfo info);
}
