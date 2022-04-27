// package com.lab.common.commands;

// import java.io.IOException;
// import java.sql.Connection;
// import java.sql.SQLException;

// import com.lab.common.data.User;
// import com.lab.common.util.BodyCommand;
// import com.lab.common.util.CollectionManager;
// import com.lab.common.util.IOManager;

// public class AuthenicateCommand extends Command {

//     @Override
//     public CommandResult run(BodyCommand bodyCommand, CollectionManager collection, Connection table, Long userID)
//             throws SQLException {
//         User newClient = (User) bodyCommand.getData();
        
//         return null;
//     }

//     @Override
//     public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) throws IOException {
//         while (true) {
//             ioManager.println("Enter username");
//             ioManager.prompt();
//             String username = ioManager.readLine().trim();
//             ioManager.println("Enter password");
//             ioManager.prompt();
//             String password = ioManager.readPassword(); //check empty line and spaces at the end
//             User client = new User(username, password);
//             return new BodyCommand(client);
//         }
//     }
// }
