package UDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client extends Thread {
    private String serverName;
    private int serverPort;
    private DatagramSocket socket;
    private InetAddress address;
    private String message;
    private byte[] buffer;

    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.message = message;
        try {
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName(serverName);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        Client client = new Client("localhost", 4445);
        Client client = new Client(System.getenv("SERVER_NAME"), Integer.parseInt(System.getenv("SERVER_PORT")));
        client.start();
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            this.message = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            message="login";
            buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);

            try {
                socket.send(packet);
                packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                socket.receive(packet);
//                System.out.println(new String(packet.getData(), 0, packet.getLength()));
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            if(message.equals("logout")){
                break;
            }

            try {
                this.message = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
