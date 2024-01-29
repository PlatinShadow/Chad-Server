package de.uulm.in.vs.vns.p6b.vnscp.server;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.ByeMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.LoginMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.PingMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.request.SendMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.response.ErrorMessage;
import de.uulm.in.vs.vns.p6b.vnscp.messages.response.LoggedInMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CommandHandler implements Runnable {

    private final Socket socket;
    private final Server server;
    private PrintWriter writer;

    public CommandHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

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

            while(true) {
                String line = reader.readLine();

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


    private void send(Message msg) {
        System.out.println("[DEBUG][CMD]: Sending " + msg.getClass().getSimpleName() + " to " + socket.getInetAddress());
        String payload = msg.serialize();
        writer.write(payload);
        writer.flush();
    }


    private void on_login(LoginMessage msg) {
        System.out.println("[DEBUG][CMD]: (" + socket.getInetAddress() + ") Received LOGIN: " + msg.get_username());

        boolean success = server.register_user(msg.get_username());

        if(success) {
            send(new LoggedInMessage(server.get_next_id()));

        } else {
            System.out.println("[ERROR][CMD]: (" + socket.getInetAddress() + ") Invalid Username " + msg.get_username());
            send(new ErrorMessage("Invalid Username"));
        }
    }

    private void on_send(SendMessage msg) {
        System.out.println("[DEBUG][CMD]: (" + socket.getInetAddress() + ") Received SEND: " + msg.get_text());

    }

    private void on_ping(PingMessage msg) {
        System.out.println("[DEBUG][CMD]: (" + socket.getInetAddress() + ") Received PING");

    }

    private void on_bye(ByeMessage msg) {
        System.out.println("[DEBUG][CMD]: (" + socket.getInetAddress() + ") Received BYE");

    }

    private void handle_request(String[] lines) {
        try {
            var message = Message.parse(lines);

            System.out.println("[DEBUG][CMD]: New " + message.getClass().getSimpleName() + " from " + socket.getInetAddress());

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
    }
}
