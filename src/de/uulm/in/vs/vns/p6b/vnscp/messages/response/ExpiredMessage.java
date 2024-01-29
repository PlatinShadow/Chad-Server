package de.uulm.in.vs.vns.p6b.vnscp.messages.response;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpiredMessage extends Message {

    private final String m_Date;

    public ExpiredMessage(int Id){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        m_Date = formatter.format(date);
    }
    @Override
    public String serialize() {
        return serialize_helper(this);
    }


}
