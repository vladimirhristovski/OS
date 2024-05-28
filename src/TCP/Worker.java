package TCP;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread {

    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        boolean logged = false;
        int numMessages = 0;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line;
            while(!(line = reader.readLine()).isEmpty()){
                System.out.println(String.format("RECIEVED(from: %s:%d) : %s",socket.getInetAddress().getHostAddress(),socket.getPort(),line));
                numMessages++;

                if (!logged) {
                    if (line.equals("login")) {
                        logged = true;
                    }else {
//                        writer.write("log in first!\n");
//                        writer.flush();
                        break;
                    }
                }

                if (logged) {
                    if (line.equals("login")) {
//                        System.out.println("logged in");
                        writer.write("logged in\n");
                    } else if (line.equals("logout")) {
//                        System.out.println("logged out");
                        writer.write("logged out\n");
                        writer.flush();
                        logged = false;
                        break;
                    } else {
//                        System.out.println(line);
                        writer.write(line+"\n");
                    }
                    writer.flush();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Server.addMessagesFromClient(numMessages);
        }
    }
}
