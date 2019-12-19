package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


public class Server {

    protected static ServerSocket serverSocket;

    public static final int port = 111;
    public static LinkedList<Torrent> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {

        serverSocket = new ServerSocket(port);

        try {
            while (true) {
                if (serverSocket.isClosed()) break;
                Socket socket = serverSocket.accept();
                try {
                    serverList.add(new Torrent(socket));

                } catch (IOException e) {
                    socket.close();
                }

            }
        } finally {
            serverSocket.close();
            serverList.clear();
        }
    }
}