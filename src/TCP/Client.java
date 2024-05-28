package TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private int serverPort;
    private String serverName;

    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
//        String serverName = System.getenv("SERVER_NAME");
//        String serverPort = System.getenv("SERVER_PORT");
//        if (serverPort == null) {
//            throw new RuntimeException("Server port should be defined as ENV {SERVER_PORT}.");
//        }
//        Client client = new Client(serverName, Integer.parseInt(serverPort));

//        Client client = new Client("localhost", 7000);
        Client client = new Client(System.getenv("SERVER_NAME"), Integer.parseInt(System.getenv("SERVER_PORT")));
        client.start();
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new Socket(InetAddress.getByName(this.serverName), this.serverPort);
            System.out.println("You are connected, log in!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
//                String input_message = scanner.nextLine();
                String input_message = "login";
                writer.write(input_message + "\n");
                writer.flush();

                String recieved = reader.readLine();
                System.out.println("RECIEVED: " + recieved);

                if (input_message.equals("logout")) {
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
