package com.thinksky.info;

/**
 * Created by Administrator on 2015/7/28 0028.
 */
public class ImageInfo {

    /**
     * src : 图片URL http://upload.opensns.cn/Uploads_Editor_Picture_2015-07-27_55b58667ce068.jpg
     * pos : 图片名字 <-IMG#0->
     */
    private String src;
    private String pos;

    public void setSrc(String src) {
        this.src = src;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getSrc() {
        return src;
    }

    public String getPos() {
        return pos;
    }
}
