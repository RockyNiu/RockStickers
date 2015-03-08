package com.rockyniu.stickers.model;

/**
 * Created by Lei on 2015/2/16.
 */
public class User extends BaseData {
    private String userName = "";
    private String password = "";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
