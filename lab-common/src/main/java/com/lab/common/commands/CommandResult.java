package com.lab.common.commands;

import java.io.Serializable;

public class CommandResult implements Serializable {
    private static final long serialVersionUID = 8711472321987691021L;
    private Serializable data;
    private Boolean resultStatus;
    private String name;
    private String messageResult;

    public CommandResult(String name, Serializable data, Boolean resultStatus, String messageResult) {
        this.setName(name);
        this.setData(data);
        this.setResultStatus(resultStatus);
        this.setMessageResult(messageResult);
    }

    public Boolean getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Boolean result) {
        this.resultStatus = result;
    }

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(String messageResult) {
        this.messageResult = messageResult;
    }
}
