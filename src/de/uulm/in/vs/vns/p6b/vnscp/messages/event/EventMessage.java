package de.uulm.in.vs.vns.p6b.vnscp.messages.event;

import de.uulm.in.vs.vns.p6b.vnscp.messages.Message;
import jdk.jfr.Description;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventMessage extends Message {

    private final int m_Id;
    private final String m_Date;

    private final String m_Description;

    public EventMessage(int Id, String Description){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        m_Date = formatter.format(date);
        m_Id = Id;
        m_Description = Description;
    }
    @Override
    public String serialize() {
        return serialize_helper(this);
    }


}

