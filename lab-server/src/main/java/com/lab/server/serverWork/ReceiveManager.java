package com.lab.server.serverWork;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lab.common.util.Message;

public class ReceiveManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveManager.class);
    private final int defaultBufferSize = 256;
    private final int defaultSleepTime = 500;
    private DatagramChannel channel;
    private SocketAddress client;

    public ReceiveManager(DatagramChannel channel, SocketAddress client) {
        this.channel = channel;
        this.client = client;
    }

    public Serializable deserialize(byte[] data) throws IOException {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            Serializable mess = (Serializable) is.readObject();
            in.close();
            is.close();
            return mess;
        } catch (ClassNotFoundException e) {
            return new Message("error", null);
        }
    }

    public Message receiveMessage() throws IOException {
        byte[] bufReceiveSize = new byte[defaultBufferSize];
        ByteBuffer receiveBufferSize = ByteBuffer.wrap(bufReceiveSize);
        client = channel.receive(receiveBufferSize);
        Message mess;
        if (Objects.nonNull(client)) {
            Serializable receiveMess = deserialize(bufReceiveSize);
            if (receiveMess.getClass().equals(Integer.class)) {
                int size = (int) receiveMess;
                try {
                    Thread.sleep(defaultSleepTime);
                } catch (InterruptedException e) {
                    return null;
                }
                byte[] bufr = new byte[size];
                ByteBuffer receiveBuffer = ByteBuffer.wrap(bufr);
                channel.receive(receiveBuffer);
                receiveMess = deserialize(bufr);
                mess = (Message) receiveMess;
                LOGGER.info("Received message: " + "\n-----------------\n" + mess.getCommand() + "\n-----------------------");
            } else {
                mess = (Message) receiveMess;
            }
            return mess;
        } else {
            return null;
        }
    }

    public SocketAddress getClient() {
        return client;
    }
}
