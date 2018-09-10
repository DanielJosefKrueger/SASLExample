package server;

import util.ScenarioVariables;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslServerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Enumeration;

public class DigestMd5Server {

    public static void main(String[] args) throws IOException {
        DigestMd5Server server = new DigestMd5Server();
        server.startServer(ScenarioVariables.HOST, ScenarioVariables.PORT);
    }

    private void startServer(String host, int port) throws IOException {
        System.out.println("Available mechanisms:");
        Enumeration<SaslServerFactory> enumeration = Sasl.getSaslServerFactories();
        while (enumeration.hasMoreElements()) {
            SaslServerFactory saslServerFactory = enumeration.nextElement();
            System.out.println(Arrays.toString(saslServerFactory.getMechanismNames(null)));
        }

        try (ServerSocket serverSocket = new ServerSocket(port, 2,  InetAddress.getByName(host))) {
            while (true) {
                Socket socket = serverSocket.accept();
                DigestMd5ConnectionHandler connectHandler = new DigestMd5ConnectionHandler(socket);
                connectHandler.start();
                System.out.println("new Client connected");
            }
        }
    }






}
