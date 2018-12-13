package com.goach.okdown.callback;

import com.goach.okdown.model.OkDownInfo;

/**
 * @author Goach
 * Date 2018/11/26
 **/
public abstract class SimpleOkDownAdapter implements OkDownListener {
    @Override
    public void onStart(OkDownInfo info) {

    }

    @Override
    public void onProgress(float progress) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onPause(OkDownInfo info) {

    }

    @Override
    public void onCancel(OkDownInfo info) {

    }

    @Override
    public void onWait(OkDownInfo info) {

    }

    @Override
    public void onFinish(OkDownInfo info) {

    }
}
