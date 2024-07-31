package test;
// imports
import java.util.Date;


public class Message {
    // define data members of Message class:
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    // first constructor: init by string
    public Message(String txt) {
        double temp;
        this.asText = txt;
        this.data = txt.getBytes();

        // in case the string is not a valid double number, asDouble will be NaN
        try {
            temp = Double.parseDouble(txt);
        } catch (NumberFormatException e) {
//            System.out.println("Error: input is not a valid double number.\n" +
//                    "initiating as NaN.");
            temp = Double.NaN;
        }
        this.asDouble = temp;

        // set date object to current date and time
        this.date = new Date();
    }

    // second constructor: init by double
    public Message(double d) {
        this(Double.toString(d));
    }

    // third constructor: init by byte array
    public Message(byte[] data) {
        this(new String(data));
    }

}
