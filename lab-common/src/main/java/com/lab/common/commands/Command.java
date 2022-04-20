package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public abstract class Command {
    public abstract CommandResult run(Object data, Object object, Connection table);
}
