package com.lab.server.serverWork;

import com.lab.common.commands.CommandResult;

public interface Callback {
    void callback(CommandResult commandResult);
}
