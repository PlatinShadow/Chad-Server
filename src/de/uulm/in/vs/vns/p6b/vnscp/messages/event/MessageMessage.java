package de.uulm.in.vs.vns.p6b.vnscp.messages.event;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageMessage extends Message {

    private final int m_Id;
    private final String m_Date;
    private final String m_Username;
    private final String m_Text;

    public MessageMessage(int Id, String username, String text){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        m_Date = formatter.format(date);
        m_Id = Id;
        m_Username = username;
        m_Text = text;
    }
    @Override
    public String serialize() {
        return serialize_helper(this);
    }


}
