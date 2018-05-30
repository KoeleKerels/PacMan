package server;

import data.ApplicationData;
import server.data.User;
import server.manager.ThreadManager;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static ApplicationData applicationData;
    private static ThreadManager threadManager;

    public static void main(String[] args) {
        applicationData = new ApplicationData();
        threadManager = new ThreadManager();
        threadManager.start();

        try {
            ServerSocket serverSocket = new ServerSocket(666);
            int count = 0;
            while (true) {
                Socket socket = serverSocket.accept();

                threadManager.addUser(new User(socket));

                count++;
                System.out.println("Count: " +count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static ApplicationData getApplicationData() {
        return applicationData;
    }
}

