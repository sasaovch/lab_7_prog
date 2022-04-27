package com.lab.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Stack;

public class IOManager {
    private BufferedReader reader;
    private PrintWriter writer;
    private String prompter;
    private Boolean fileMode = false;
    private final Stack<BufferedReader> previosReaders = new Stack<>();
    private final Stack<File> currentFiles = new Stack<>();

    public IOManager() {
        this(System.in, System.out, "$");
    }

    public IOManager(InputStream input, OutputStream output, String promter) {
        this.reader = new BufferedReader(new InputStreamReader(input));
        this.writer = new PrintWriter(output, true);
        this.prompter = promter;
    }


    public  void setBufferReader(BufferedReader buf) {
        reader = buf;
    }

    public  void setPrintWriter(PrintWriter wr) {
        writer = wr;
    }

    public  BufferedReader getBufferedReader() {
        return reader;
    }

    public  PrintWriter getPrintWriter() {
        return writer;
    }

    public void turnOnFileMode(String filename) {
        try {
            File file = new File(filename);
            if (file.exists() && !currentFiles.contains(file)) {
                    BufferedReader newReader = new BufferedReader(new FileReader(file));
                    println("Started to execute script: " + file.getName());
                    println("------------------------------------------");
                    currentFiles.push(file);
                    previosReaders.push(getBufferedReader());
                    setBufferReader(newReader);
                    fileMode = true;
            } else if (!file.exists()) {
                    printerr("File doesn't exist.");
            } else if (currentFiles.contains(file)) {
                    printerr("The file was not executed due to recursion.");
                    turnOffFileMode();
                }
        } catch (FileNotFoundException e) {
            printerr("Invalid file access rights.");
        }
    }

    public void turnOffFileMode() {
        File file = currentFiles.pop();
        setBufferReader(previosReaders.pop());
        if (currentFiles.isEmpty()) {
            fileMode = false;
        }
        println("------------------------------------------");
        println("Finished to execute script: " + file.getName());
    }

    public Boolean getFileMode() {
        return fileMode;
    }

    public  String readLine() throws IOException {
        return reader.readLine();
    }

    public String readPassword() throws IOException {
        return new String(System.console().readPassword());
    }

    public String readfile(File file) throws FileNotFoundException, IOException {
        StringBuilder strData = new StringBuilder();
        String line;
        if (!file.exists()) {
            throw new FileNotFoundException();
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                while ((line = bufferedReader.readLine()) != null) {
                    strData.append(line);
                }
            }
        }
        return strData.toString();
    }

    public  void close() throws IOException {
        reader.close();
        writer.close();
    }

    public void prompt() {
        writer.printf("%s", prompter);
    }

    public  void print(Object o) {
        writer.printf("%s", o);
    }

    public  void println(Object o) {
        writer.println(o);
    }

    public  void printerr(Object o) {
        writer.println("Error! " + o);
    }
}
