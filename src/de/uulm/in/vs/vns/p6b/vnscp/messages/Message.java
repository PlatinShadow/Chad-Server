package de.uulm.in.vs.vns.p6b.vnscp.messages;

import de.uulm.in.vs.vns.p6b.vnscp.base.Version;
import de.uulm.in.vs.vns.p6b.vnscp.exceptions.InvalidHeaderException;
import de.uulm.in.vs.vns.p6b.vnscp.exceptions.InvalidMessageException;
import de.uulm.in.vs.vns.p6b.vnscp.exceptions.InvalidMessageFormatException;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Field;

public abstract class Message {
    /**
     * Parse a Message into the correct Message Object
     * @param lines lines of the received message
     * @return Request Message Object
     */
    public static Object parse(String[] lines) {
        if(lines.length == 0) {
            throw new InvalidMessageException("Empty Message");
        }

        String[] header = lines[0].split(" ");
        if(!header[1].equals(Version.VERSION_STRING)) {
            throw new InvalidHeaderException(lines[0]);
        }

        var message = switch (header[0]) {
            case "LOGIN" -> new Object();
            case "BYE" -> new Object();
            case "SEND" -> new Object();
            case "PING" -> new Object();
            default -> throw new InvalidMessageException(header[0]);
        };

        var messageClass = message.getClass();

        for(int i = 1; i < lines.length; i++) { // i=1 -> skip header
            String[] key_value = lines[i].split(":");

            if(key_value.length != 2) {
                throw new InvalidMessageException("Invalid Format");
            }

            key_value[1] = key_value[1].trim();

            // Parse field name
            String field_name = "m_" + key_value[0];
            Field field = null;

            // Try to get field
            try {
                field = messageClass.getDeclaredField(field_name);
            } catch (NoSuchFieldException e) {
                throw new InvalidMessageFormatException(key_value[0]);
            }

            // Set Field Value
            try {

                field.setAccessible(true);
                if (field.getType().isPrimitive()) {
                    int value = Integer.parseInt(key_value[1]);
                    field.set(message, value);
                } else {
                    field.set(message, key_value[1]);
                }
            } catch(IllegalAccessException e) {
                throw new RuntimeException("Could not set field");
            }
        }

        return message;
    }

    /**
     * Serializes a Message into a string using reflection
     * @param obj The object to serialize
     * @return The serializes form of the message
     */
    protected String serialize_helper(Object obj) {
        // Get name of Message by using the class name
        String message_name = obj.getClass().getSimpleName();
        message_name = message_name.substring(0, message_name.lastIndexOf("Message"));

        // Build the header
        StringBuilder sb = new StringBuilder();
        sb.append(Version.VERSION_STRING);
        sb.append(" ");
        sb.append(message_name.toUpperCase());
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
                throw new RuntimeException("Could not set field");
            }

            // new line
            sb.append("\r\n");
        }

        // End of message terminated with extra newline
        sb.append("\r\n");

        return sb.toString();
    }

    /**
     * Serialize Message to String conform with Protocol
     * @return Serialized Message
     */
    public abstract String serialize();
}
