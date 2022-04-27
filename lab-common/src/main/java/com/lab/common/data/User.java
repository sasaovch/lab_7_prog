package com.lab.common.data;

import java.io.Serializable;

public class User implements Serializable{
    private static final long serialVersionUID = 6813517110395654951L;
    private String login;
    private String password;
    private String salt;
    private boolean authenticationStatus = false;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login, String password, String salt) {
        this(login, password);
        this.salt = salt;
    }

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
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
}
