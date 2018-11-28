package com.goach.okdown.data;

/**
 * @author Goach
 * Date: 2018/11/26
 * des: 一些常量
 **/
public interface OkDownConst {
    int NONE = 0;//空闲中
    int START = 1;//开始下载
    int DOWNLOADING = 2;//下载中
    int PAUSE = 3;//暂停中
    int RESUME = 4;//恢复下载
    int RESET = 5;//重新下载
    int CANCEL = 6;//取消下载
    int FINISH = 7;//下载完成
    int ERROR = 8;//下载失败
    int WAIT = 9;//等待下载
    int DESTROY = 10;//释放资源
}
