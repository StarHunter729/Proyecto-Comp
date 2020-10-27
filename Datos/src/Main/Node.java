package Main;

import java.io.IOException;
import java.net.UnknownHostException;

import Server.Server;

public class Node {

    public static void main(String args[]) throws UnknownHostException, IOException {
        Server Node = new Server();
        Node.getServerSocket();
        Node.connectToExisting();
        Node.keepListening();
    }
}
