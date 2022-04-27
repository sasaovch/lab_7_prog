// package com.lab.common.commands;

// import java.sql.SQLException;

// import com.lab.common.util.BodyCommand;

// public class ExecuteScriptCommand extends Command{

//     public void turnOnFileMode(String filename) {
//         try {
//             File file = new File(filename);
//             if (file.exists() && !currentFiles.contains(file)) {
//                     BufferedReader newReader = new BufferedReader(new FileReader(file));
//                     println("Started to execute script: " + file.getName());
//                     println("------------------------------------------");
//                     currentFiles.push(file);
//                     previosReaders.push(getBufferedReader());
//                     setBufferReader(newReader);
//                     fileMode = true;
//             } else if (!file.exists()) {
//                     printerr("File doesn't exist.");
//             } else if (currentFiles.contains(file)) {
//                     printerr("The file was not executed due to recursion.");
//                     turnOffFileMode();
//                 }
//         } catch (FileNotFoundException e) {
//             printerr("Invalid file access rights.");
//         }
//     }

//     public void turnOffFileMode() {
//         File file = currentFiles.pop();
//         setBufferReader(previosReaders.pop());
//         if (currentFiles.isEmpty()) {
//             fileMode = false;
//         }
//         println("------------------------------------------");
//         println("Finished to execute script: " + file.getName());
//     }

//     @Override
//     public CommandResult run(BodyCommand bodyCommand, Long userID) throws SQLException {
//         // TODO Auto-generated method stub
//         return null;
//     }

// }
