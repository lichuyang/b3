package com.lcy.baidusou.bean;

/**
 * Created by lcy on 2017/2/4.
 */
public class SearchResult extends Base{
    private static final long serialVersionUID = 7231338588202254474L;
    private String title;
    private String id;
    private String userId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
