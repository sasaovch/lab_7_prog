package com.lab.common.commands;

import java.io.Serializable;

public class CommandResult implements Serializable {
    private static final long serialVersionUID = 8711472321987691021L;
    private Serializable data;
    private Boolean resultStatus;
    private String commandName;
    private String messageResult;

    public CommandResult(String commandName, Serializable data, Boolean resultStatus, String messageResult) {
        this.setCommandName(commandName);
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

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getMessageResult() {
        return messageResult;
    }

    public void setMessageResult(String messageResult) {
        this.messageResult = messageResult;
    }
}
