package de.uulm.in.vs.vns.p6b.vnscp.server;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Server {

    ServerSocket command_socket;
    ServerSocket event_socket;

    ExecutorService thread_pool;

    ArrayList<PrintWriter> event_sockets;
    ArrayList<String> user_names;

    int id = 0;

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
        user_names = new ArrayList<>();
        event_sockets = new ArrayList<PrintWriter>();
    }

    /**
     * Register a new username
     * @param username username to register
     * @return true if user was registered
     */
    synchronized boolean register_user(String username) {
        int length = username.length();
        if(!(Pattern.matches("[0-9a-zA-Z]+", username) && length <= 15 && length >= 3)) return false;
        if (user_names.contains(username))return false;
        user_names.add(username);
        return true;
    }

    /**
     * Delete a user from the user list
     * @param username user to delete
     */
    synchronized void unregister_user(String username) {
        user_names.remove(username);
    }

    /**
     * Broadcast event message to all event sockets
     * @param message Message To Broadcast
     */
    synchronized void broadcast_event(Message message) {
        String payload = message.serialize();
        for (PrintWriter w: event_sockets) {
            w.write(payload);
            w.flush();
        }
    }

    /**
     * Get the next id for a message
     * @return next id
     */
    synchronized int get_next_id() {
        id++;
        return id;
    }

    /**
     * Opens up the Pub/Sub and Command Socket
     */
    public void run() {
        System.out.println("[INFO]: Server Running!");
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


    /**
     * Acceot new sockets from the command socket
     */
    private void accept_command_sockets() {
        try {
            var socket = command_socket.accept();
            System.out.println("[INFO][CMD]: New Client IP=" + socket.getInetAddress());
            thread_pool.execute(new CommandHandler(socket, this));
        } catch(IOException e) {
            System.err.println("[ERROR]: Error while accepting socket: " + e.getMessage());
        }
    }

    /**
     * Accept new socket connections from the Event Socket
     */
    private void accept_event_sockets() {
        try {
            var socket = event_socket.accept();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            event_sockets.add(writer);

            System.out.println("[INFO][EVENT]: New Client IP=" + socket.getInetAddress());

        } catch(IOException e) {
            System.err.println("[ERROR]: Error while accepting socket: " + e.getMessage());
        }
    }

}
