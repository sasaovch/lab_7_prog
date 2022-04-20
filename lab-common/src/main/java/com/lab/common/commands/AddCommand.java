package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class AddCommand extends Command {

    @Override
    public CommandResult run(Object data, Object object, Connection table) {
        Statement stat = table.createStatement();
        String nameTable = table.getMetaData().getDatabaseProductName();
        st = conn.createStatement();
			if(st.executeUpdate("INSERT INTO worker(name, surname, phone_number) VALUES "
					+ "('"+name+"','"+surname+"','"+phoneNumber+"')") > 0) {
				state = true;
			}
			
			st.close();
        ResultSet res = stat.executeQuery("INSERT INTO " + nameTable + " VALUES (" 
                        +);
        if (collection.addElement(spMar)) {
            return new CommandResult("add", spMar.getName() + " has been successfuly added.", true);
        } else {
            return new CommandResult("add", spMar.getName() + " hasn't been added.", false);
        }
    }
}
