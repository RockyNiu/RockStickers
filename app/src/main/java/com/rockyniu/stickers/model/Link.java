package com.rockyniu.stickers.model;

/**
 * Created by Lei on 2015/2/15.
 */
public class Link extends BaseData {
    private String userId = "";
    private String address;
    private String title;
    private int linkType;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public String toSmsMessage() {
        return getTitle() + "\n" + getAddress();
    }
}
