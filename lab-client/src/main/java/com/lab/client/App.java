package com.lab.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.lab.common.exception.CommandArgumentException;
import com.lab.common.exception.IncorrectData;
import com.lab.common.exception.IncorrectDataOfFileException;
import com.lab.common.util.IOManager;
import com.lab.common.util.ParsFromVR;

public final class App {
    private  static final Integer DEFAULT_PORT = 8713;
    private  static final Integer DEFAULT_TIME_OUT = 1000;

    private App() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException, IncorrectDataOfFileException, IncorrectData, ClassNotFoundException, InterruptedException, CommandArgumentException {
        Thread.sleep(1000);
        InetAddress address = ParsFromVR.getFromVR("address", InetAddress.getLocalHost(), (variable, defaultVar) -> {
            try {
                return InetAddress.getByName(variable);
            } catch (UnknownHostException e) {
                return defaultVar;
            }
        });
        Integer port = ParsFromVR.getFromVR("port", DEFAULT_PORT, (variable, defaultVar) -> {
            try {
                return Integer.parseInt(variable);
            } catch (NumberFormatException e) {
                return defaultVar;
            }
        });
        IOManager ioManager = new IOManager();
        DatagramSocket socket = new DatagramSocket();
        ReceiveManager receiveManager = new ReceiveManager(socket);
        SendManager sendManager = new SendManager(address, socket, port);
        receiveManager.setTimeout(DEFAULT_TIME_OUT);
        Console console = new Console(ioManager, receiveManager, sendManager);
        console.run();
    }
}
