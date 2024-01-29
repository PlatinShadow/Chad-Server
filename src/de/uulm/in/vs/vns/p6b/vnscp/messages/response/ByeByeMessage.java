package de.uulm.in.vs.vns.p6b.vnscp.messages.response;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ByeByeMessage extends Message{

    private final int m_Id;
    private final String m_Date;

    public ByeByeMessage(int Id){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        m_Date = formatter.format(date);
        m_Id = Id;
    }
    @Override
    public String serialize() {
        return serialize_helper(this);
    }


}
