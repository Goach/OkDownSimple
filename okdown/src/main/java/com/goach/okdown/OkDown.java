package com.goach.okdown;

import com.goach.okdown.download.OkDownManager;

/**
 * @author Goach
 * Date 2018/11/26
 **/
public class OkDown {

    public static OkDownBuilder newBuilder(){
        return new OkDownBuilder();
    }
    public static class OkDownBuilder{
        private String url;
        private String path;
        private String name;
        private int childTaskCount;

        public OkDownBuilder url(String url){
            this.url = url;
            return this;
        }
        public OkDownBuilder path(String path){
            this.path = path;
            return this;
        }
        public OkDownBuilder name(String name){
            this.name = name;
            return this;
        }
        public OkDownBuilder childTaskCount(int childTaskCount){
            this.childTaskCount = childTaskCount;
            return this;
        }
        public OkDownManager build() {
            OkDownManager downloadManager = OkDownManager.getInstance();
            downloadManager.init(url,path,name,childTaskCount);
            return downloadManager;
        }

    }
}
