package com.example.y84104146.mediaplayer;

import org.litepal.crud.DataSupport;

public class UserInfo extends DataSupport{
    private String nikeName;
    private String telePhoneNumber;
    private String password;

    public String getNikeName() {
        return nikeName;
    }

    public String getPassword() {
        return password;
    }

    public String getTelePhoneNumber() {
        return telePhoneNumber;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelePhoneNumber(String telePhoneNumber) {
        this.telePhoneNumber = telePhoneNumber;
    }
}
