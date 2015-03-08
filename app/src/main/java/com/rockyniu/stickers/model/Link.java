package com.rockyniu.stickers.model;

/**
 * Created by Lei on 2015/2/15.
 */
public class Link extends BaseData {
    private String userId = "";
    private String link;
    private String text;
    private int linkType;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }
}
