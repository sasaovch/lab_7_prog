package com.lab.common.data;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 6813517110395654951L;
    private String username;
    private String password;
    private String salt;
    private boolean authenticationStatus = false;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String setUserName) {
        this.username = setUserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public boolean getAuthenticationStatus() {
        return authenticationStatus;
    }

    public void setAuntificationStatusTrue() {
        authenticationStatus = true;
    }

    public void setAuntificationStatusFalse() {
        authenticationStatus = false;
    }

    @Override
    public String toString() {
        return username;
    }
}
