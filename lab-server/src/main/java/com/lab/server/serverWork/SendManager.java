package com.lab.server.serverWork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lab.common.commands.CommandResult;

public class SendManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendManager.class);
    private DatagramChannel channel;
    private SocketAddress client;
    private final int limitSend = 100;

    public SendManager(DatagramChannel channel, SocketAddress client) {
        this.channel = channel;
        this.client = client;
    }

    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        byte[] outMess = out.toByteArray();
        out.close();
        os.close();
        return outMess;
    }

    public Boolean sendCommResult(CommandResult result) {
        try {
            byte[] bufs = serialize(result);
            byte[] bufSendSize = serialize(bufs.length);
            int sendSize = bufSendSize.length;
            ByteBuffer sendBufferSize = ByteBuffer.wrap(bufSendSize);
            int limit = limitSend;
            while (channel.send(sendBufferSize, client) < sendSize) {
                limit -= 1;
                if (limit == 0) {
                    return false;
                }
            }
            sendBufferSize.clear();
            ByteBuffer sendBuffer = ByteBuffer.wrap(bufs);
            sendSize = bufs.length;
            limit = limitSend;
            while (channel.send(sendBuffer, client) < sendSize) {
                limit -= 1;
                if (limit == 0) {
                    return false;
                }
            }
            sendBuffer.clear();
            return true;
        } catch (IOException e) {
            LOGGER.error("Server couldn't send message.", e);
            return false;
        }
    }

    public void setClient(SocketAddress client) {
        this.client = client;
    }
}
