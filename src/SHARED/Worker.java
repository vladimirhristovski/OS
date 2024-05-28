package SHARED;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Worker extends Thread {

    private static Semaphore counterSemaphore = new Semaphore(1);
    private Socket socket;
    private File logFile;
    private File clientsCountFile;

    public Worker(Socket socket, File logFile, File clientsCountFile) {
        this.socket = socket;
        this.logFile = logFile;
        this.clientsCountFile = clientsCountFile;
    }

    private void execute() throws IOException, InterruptedException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.logFile, true)));
        ;
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        RandomAccessFile clientCounterRaf = new RandomAccessFile(this.clientsCountFile, "rw");
        Integer currentCounterValue = incrementCounter(clientCounterRaf);
        System.out.printf("Total Number of Clients until now: %d.\n", currentCounterValue);
        String line = null;
        try {
            while ((line = socketReader.readLine()) != null) {
                //The append()/write() methods of the java.io Writer.java class are thread-safe by themselves! We do not need a synchronization.
                writer.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.flush();
                writer.close();
                socketReader.close();
                clientCounterRaf.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Integer incrementCounter(RandomAccessFile clientCounterRaf) throws InterruptedException, IOException {
        counterSemaphore.acquire();
        Integer currentClientsCounter = 0;
        try {
            currentClientsCounter = clientCounterRaf.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentClientsCounter++;
        clientCounterRaf.seek(0);
        clientCounterRaf.writeInt(currentClientsCounter);
        counterSemaphore.release();
        return currentClientsCounter;
    }
}
