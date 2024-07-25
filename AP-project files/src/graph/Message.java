package graph;

import java.util.Date;

/**
 * The Message class represents a message in the system.
 * It can be initialized with a string, double, or byte array,
 * and provides different representations of the data.
 */
public class Message {
    /** The raw byte data of the message. */
    public final byte[] data;
    /** The message content as a string. */
    public final String asText;
    /** The message content as a double, if applicable. */
    public final double asDouble;
    /** The creation timestamp of the message. */
    public final Date date;

    /**
     * Constructs a Message object from a string.
     *
     * @param txt The string content of the message.
     */
    public Message(String txt) {
        double temp;
        this.asText = txt;
        this.data = txt.getBytes();

        // in case the string is not a valid double number, asDouble will be NaN
        try {
            temp = Double.parseDouble(txt);
        } catch (NumberFormatException e) {
            temp = Double.NaN;
        }
        this.asDouble = temp;

        // set date object to current date and time
        this.date = new Date();
    }

    /**
     * Constructs a Message object from a double value.
     *
     * @param d The double value to be stored in the message.
     */
    public Message(double d) {
        this(Double.toString(d));
    }

    /**
     * Constructs a Message object from a byte array.
     *
     * @param data The byte array to be stored in the message.
     */
    public Message(byte[] data) {
        this(new String(data));
    }

    /**
     * Retrieves the content of the message as a string.
     *
     * @return The string representation of the message content.
     */
    public String getContent() {
        return this.asText;
    }
}