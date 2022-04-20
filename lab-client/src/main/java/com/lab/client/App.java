package com.lab.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.lab.common.exception.IncorrectData;
import com.lab.common.exception.IncorrectDataOfFileException;
import com.lab.common.util.ConvertVR;
import com.lab.common.util.Message;

public final class App {
    private  static final Integer DEFAULT_PORT = 8713;
    private  static final Integer DEFAULT_TIME_OUT = 1000;

    private App() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException, IncorrectDataOfFileException, IncorrectData, ClassNotFoundException, InterruptedException {
        InetAddress address = getFromVR("address", (variable, defaultVar) -> {
            try {
                return InetAddress.getByName(variable);
            } catch (UnknownHostException e) {
                return defaultVar;
            }
        }, InetAddress.getLocalHost());
        Integer port = getFromVR("port", (variable, defaultVar) -> {
            try {
                return Integer.parseInt(variable);
            } catch (NumberFormatException e) {
                return defaultVar;
            }
        }, DEFAULT_PORT);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(System.out, true);
        IOManager ioManager = new IOManager(reader, writer, "$");
        AskMarine asker = new AskMarine(ioManager);
        ParsCommand parsingComm = new ParsCommand(ioManager, asker);
        Map<String, Function<String, Message>> parsingList = new HashMap<>();
        parsingList = initializeParsingList(parsingList, parsingComm);
        DatagramSocket socket = new DatagramSocket();
        SendManager sendManager = new SendManager(address, socket, port);
        ReceiveManager receiveManager = new ReceiveManager(socket);
        receiveManager.setTimeout(DEFAULT_TIME_OUT);
        Console console = new Console(parsingList, ioManager, receiveManager, sendManager);
        console.run();
    }

    public static <T> T getFromVR(String name, ConvertVR<T> funct, T defaultParam) {
        String variable = System.getenv(name);
        if (Objects.isNull(variable)) {
            return defaultParam;
        }
        return funct.convert(variable, defaultParam);
    }

    public static Map<String, Function<String, Message>> initializeParsingList(Map<String, Function<String, Message>> parsingList, ParsCommand parsingComm) {
        parsingList.put("help", parsingComm::helpComm);
        parsingList.put("info", parsingComm::infoComm);
        parsingList.put("show", parsingComm::showComm);
        parsingList.put("clear", parsingComm::clearComm);
        parsingList.put("exit", parsingComm::exitComm);
        parsingList.put("group_counting_by_name", parsingComm::groupCountingByNameComm);
        parsingList.put("print_descending", parsingComm::printDescendingComm);
        parsingList.put("add", parsingComm::addComm);
        parsingList.put("add_if_min", parsingComm::addIfMinComm);
        parsingList.put("remove_greater", parsingComm::removeGreaterComm);
        parsingList.put("remove_lower", parsingComm::removeLowerComm);
        parsingList.put("update", parsingComm::updateComm);
        parsingList.put("remove_by_id", parsingComm::removeByIdComm);
        parsingList.put("count_by_loyal", parsingComm::countByLoyalComm);
        parsingList.put("execute_script", parsingComm::executeScriptComm);
        return parsingList;
    }
}
