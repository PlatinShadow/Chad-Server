package de.uulm.in.vs.vns.p6b.vnscp.server;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

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
                    parse_request(lines.toArray(String[]::new));
                    lines.clear();
                } else {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void send(Message msg) {
        String payload = msg.serialize();
        writer.write(payload);
        writer.flush();
    }

    private void parse_request(String[] lines) {
        try {
            var message = Message.parse(lines);



        } catch (Exception e) {
            send(new ErrorMessage(e.getMessage()));
        }
    }
}
