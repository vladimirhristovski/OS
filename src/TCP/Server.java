package TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private int port;
    private static int messagesRecieved;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
//        Server server = new Server(7000);
        Server server = new Server(Integer.parseInt(System.getenv("SERVER_PORT")));
        server.start();
    }

    @Override
    public void run() {
        System.out.println("SERVER: staring...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("SERVER: socket opened...");

            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("NEW CLIENT AND THREAD");
                Worker worker = new Worker(socket);
                worker.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void addMessagesFromClient(int num){
        messagesRecieved+=num;
        System.out.println("MESSAGES AFTER CLIENT: "+ messagesRecieved);
    }
}
