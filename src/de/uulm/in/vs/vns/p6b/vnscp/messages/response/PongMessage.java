package de.uulm.in.vs.vns.p6b.vnscp.messages.response;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PongMessage extends Message {

    private final String m_Usernames;
    private final String m_Date;

    public PongMessage(List<String> usernameList ){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        m_Date = formatter.format(date);
        String usernameString = usernameList.toString();
        m_Usernames = usernameString.substring(1, usernameString.length() -1);
    }
    @Override
    public String serialize() {
        return serialize_helper(this);
    }


}
