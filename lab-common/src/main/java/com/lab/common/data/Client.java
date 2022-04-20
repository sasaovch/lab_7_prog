package com.lab.common.data;

public class Client {
    private String login;
    private String password;
    private String salt;

    public Client(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Client(String login, String password, String salt) {
        this.login = login;
        this.password = password;
        this.salt = salt;
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
}
