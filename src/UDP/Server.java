package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {
    private DatagramSocket socket;
    private byte[] buffer;

    public Server(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.buffer = new byte[256];
    }

    public static void main(String[] args) {
//        Server server = new Server(4445);
        Server server = new Server(Integer.parseInt(System.getenv("SERVER_PORT")));
        server.start();
    }

    @Override
    public void run() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        boolean logged = false;
        while (true) {
            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                if (!logged) {
                    if (received.equals("login")) {
                        logged = true;
                    }
                }

                packet = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
                socket.send(packet);

                if (logged) {
                    if (received.equals("login")) {
                        System.out.println("logged in");
                    } else if (received.equals("logout")) {
                        System.out.println("logged out");
                        logged = false;
                        break;
                    } else {
                        System.out.println(received);
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }
}
