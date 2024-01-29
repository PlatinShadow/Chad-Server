package de.uulm.in.vs.vns.p6b.vnscp.server;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;
import de.uulm.in.vs.vns.p6b.vnscp.messages.event.EventMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.event.MessageMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.ByeMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.LoginMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.PingMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.SendMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.response.*;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CommandHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private PrintWriter writer;
    private String username;
    private boolean is_open = true;
    private long last_message_time = System.currentTimeMillis();

    private boolean is_logged_in = false;

    /**
     * Constructor for Session Handler
     * @param socket socket of the client
     * @param server server instance
     */
    public CommandHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * Continuously listens for messages from the client
     */
    @Override
    public void run() {
        try {
            writer = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream())
            );

            var reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            var lines = new ArrayList<String>();

            while(is_open) {
                String line = reader.readLine();
                if(!is_open) {
                    return;
                }


                if(line.isEmpty()) {
                    // Handle the request
                    handle_request(lines.toArray(String[]::new));
                    lines.clear();
                } else {

                    // Accumulate request lines
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            System.err.println("[ERROR][CMD]: " + e.getMessage() + " | IP=" + socket.getInetAddress());
        }
    }


    /**
     * Sends a message to the current client of this session
     * @param msg message to send
     */
    private void send(Message msg) {
        System.out.println("[DEBUG][CMD]: Sending " + msg.getClass().getSimpleName() + " to " + socket.getInetAddress());
        String payload = msg.serialize();
        writer.write(payload);
        writer.flush();
    }


    /**
     * Triggered when the client sends the Login Message
     * @param msg Login Message
     */
    private void on_login(LoginMessage msg) {
        System.out.println("[DEBUG][CMD]: (" + socket.getInetAddress() + ") Received LOGIN: " + msg.get_username());

        boolean success = server.register_user(msg.get_username());

        if(success) {
            int id = server.get_next_id();
            username = msg.get_username();
            is_logged_in = true;
            send(new LoggedInMessage(id));
            server.broadcast_event(new EventMessage(id, username + " has joined the server"));
        } else {
            System.out.println("[ERROR][CMD]: (" + socket.getInetAddress() + ") Invalid Username " + msg.get_username());
            send(new ErrorMessage("Invalid Username"));
        }
    }

    /**
     * Triggered when the cleint Send the SendMessage message
     * @param msg SendMessage Message
     */
    private void on_send(SendMessage msg) {
        int id = server.get_next_id();
        System.out.println("[DEBUG][CMD]: (" + socket.getInetAddress() + ") Received SEND: " + msg.get_text());
        server.broadcast_event(new MessageMessage(id, username, msg.get_text()));
        send(new SentMessage(id));
    }

    /**
     * Triggered when the client sends the Ping message
     * @param msg Ping Message
     */
    private void on_ping(PingMessage msg) {
        PongMessage response = new PongMessage(server.user_names);
        send(response);
    }

    /**
     * Triggered when the user sends the bye message
     * @param msg Bye Message
     */
    private void on_bye(ByeMessage msg) {
        int id = server.get_next_id();
        ByeByeMessage response = new ByeByeMessage(id);
        send(response);
        close_session(id);
    }

    /**
     * Parse and handle a raw message
     * @param lines Lines of the message
     */
    private void handle_request(String[] lines) {
        try {

            // Check timeout
            if(System.currentTimeMillis() - last_message_time > 10 * 60 * 1000) {
                int id = server.get_next_id();
                send(new ExpiredMessage(id));
                server.broadcast_event(new EventMessage(id, username + " has left the server"));
                return;
            }

            // Parse Message
            var message = Message.parse(lines);

            System.out.println("[DEBUG][CMD]: New " + message.getClass().getSimpleName() + " from " + socket.getInetAddress());

            // Don't let client send messsage before login
            if(!(message instanceof  LoginMessage) && !is_logged_in) {
                send(new ErrorMessage("Login first please"));
                System.out.println("[ERROR][CMD]: Client tried to send commands before establishing session | IP=" + socket.getInetAddress());
            }

            // Trigger the right handler depending on message type
            if(message instanceof LoginMessage) {
                on_login((LoginMessage) message);
            } else if(message instanceof SendMessage) {
                on_send((SendMessage) message);
            } else if(message instanceof PingMessage) {
                on_ping((PingMessage) message);
            } else if (message instanceof ByeMessage) {
                on_bye((ByeMessage) message);
            } else {
                send(new ErrorMessage("Unknown Request"));
                System.err.println("[ERROR][CMD]: Client Sent Unknown Request | IP=" + socket.getInetAddress());
            }


        } catch (Exception e) {
            send(new ErrorMessage(e.getMessage()));
            System.err.println("[ERROR][CMD]: " + "Error while processing Message: " + e.getMessage() + " | IP=" + socket.getInetAddress());
        }

        // Update last timestamp
        last_message_time = System.currentTimeMillis();
    }

    /**
     * Closes the socket
     * @param id ID of the event
     */
    private void close_session(int id) {
        server.broadcast_event(new EventMessage(id, username + " has left the server"));
        server.unregister_user(username);
        is_open = false;

        try {
            socket.close();
        } catch (Exception e) {

        }
    }

}
