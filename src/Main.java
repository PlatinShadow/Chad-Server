import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;
import de.uulm.in.vs.vns.p6b.vnscp.messages.response.ErrorMessage;
import de.uulm.in.vs.vns.p6b.vnscp.server.Server;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        Server server = new Server(8122, 8123);

        try {
            server.run();
        } catch (Exception e) {
            System.out.println("[Error]: " + e.getMessage());
        }

    }
}