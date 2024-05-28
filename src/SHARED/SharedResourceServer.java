package SHARED;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SharedResourceServer extends Thread {

    private String csvFile;
    private String counterFile;
    private int port;

    public SharedResourceServer(int port, String csvFile, String counterFile) {
        this.csvFile = csvFile;
        this.counterFile = counterFile;
        this.port = port;
    }

    public static void main(String[] args) {
        String serverPort = System.getenv("SERVER_PORT");
        if (serverPort == null || serverPort.isEmpty()) {
            throw new RuntimeException("Please add env variable with port number.");
        }
        SharedResourceServer server = new SharedResourceServer(Integer.parseInt(serverPort),
                System.getenv("logFile"),
                System.getenv("counterFile"));
        server.start();
    }

    @Override
    public void run() {
        System.out.println("Shared Resource Server: staring...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Shared Resource Server: started!");
        System.out.println("Shared Resource Server: waiting for connections...");

        while (true) {
            Socket socket = null;
            try {
                //accept metodot e blokiracki
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("SERVER: new client");
            new Worker(socket, new File(csvFile), new File(counterFile)).start();
        }
    }
}
