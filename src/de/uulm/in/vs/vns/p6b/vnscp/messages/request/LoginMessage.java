package de.uulm.in.vs.vns.p6b.vnscp.messages.request;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

public class LoginMessage extends Message {
    private String m_Username;

    public String get_username(){
        return m_Username;
    }

    @Override
    public String serialize() {
        return null;
    }
}
