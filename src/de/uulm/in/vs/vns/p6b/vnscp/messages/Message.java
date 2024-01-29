package de.uulm.in.vs.vns.p6b.vnscp.messages;

import de.uulm.in.vs.vns.p6b.vnscp.base.Version;

public abstract class Message {
    Object parse() {

    }

    /**
     * Serializes a Reqest Message into a string using refelction
     * @param obj The object to serialize
     * @return The serializes form of the message
     */
    protected String serialize_helper(Object obj) {
        // Get name of Message by using the class name
        String message_name = obj.getClass().getSimpleName();
        message_name = message_name.substring(0, message_name.indexOf("Request"));

        // Build the header
        StringBuilder sb = new StringBuilder();
        sb.append(message_name.toUpperCase());
        sb.append(" ");
        sb.append(Version.VERSION_STRING);
        sb.append("\r\n");

        // Iterate through every field and only add fields starting with m_
        var fields = obj.getClass().getDeclaredFields();
        for(var field : fields) {
            if(!field.getName().startsWith("m_")) {
                continue;
            }

            // Append field name
            sb.append(field.getName().substring(2));
            sb.append(": ");

            // Append field value
            try {
                field.setAccessible(true);
                sb.append(field.get(obj));
            } catch (IllegalAccessException ex) {

            }

            // new line
            sb.append("\r\n");
        }

        // End of message terminated with extra newline
        sb.append("\r\n");

        return sb.toString();
    }

    public abstract String serialize();
}
