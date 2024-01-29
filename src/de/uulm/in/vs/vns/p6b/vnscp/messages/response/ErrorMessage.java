package de.uulm.in.vs.vns.p6b.vnscp.messages.response;
import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorMessage extends Message {

    private String m_Date;
    private final String m_Reason;

    public ErrorMessage(String reason){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        m_Date = formatter.format(date);
        m_Reason = reason;
    }
    @Override
    public String serialize() {
        return serialize_helper(this);
    }
}
