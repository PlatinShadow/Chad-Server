package de.uulm.in.vs.vns.p6b.vnscp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    ServerSocket command_socket;
    ServerSocket event_socket;

    ExecutorService thread_pool;

    ArrayList<Socket> event_sockets;

    /**
     * Creates a new VNSCP Server with the given ports
     * The Sockets are opened with the run method
     * @param command_port Port of the Command Socket
     * @param event_port Port of the Event Socket
     */
    public Server(int command_port, int event_port) throws IOException {
        command_socket = new ServerSocket(command_port);
        event_socket = new ServerSocket(event_port);
        thread_pool = Executors.newCachedThreadPool();
    }

    /**
     * Opens up the Pub/Sub and Command Socket
     */
    public void run() {
        thread_pool.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    accept_command_sockets();
                }
            }
        });

        thread_pool.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    accept_event_sockets();
                }
            }
        });
    }

    private void accept_command_sockets() {
        try {
            var socket = command_socket.accept();
            thread_pool.execute(new CommandHandler(socket, this));
        } catch(IOException e) {
            System.err.println("Error while accepting socket: " + e.getMessage());
        }
    }

    private void accept_event_sockets() {
        try {
            var socket = command_socket.accept();

            System.out.println("Event SOcket Cooneceted!!! IMPLEMENT ME");

        } catch(IOException e) {
            System.err.println("Error while accepting socket: " + e.getMessage());
        }
    }

}
