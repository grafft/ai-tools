package ru.isa.ai.tests.mipt.inet;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Author: Aleksandr Panov
 * Date: 11.04.2014
 * Time: 11:36
 */
public class EchoMultiServer {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java EchoMultiServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new EchoMultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
