import java.io.Serializable;

/**
 * This class stores the state and functionality for a message sent between users.
 * The main purpose for this class is the getter methods, so that it's easy to see if a user has unread messages.
 */
public class Message implements Serializable {
	/**
	 * The fields of a message. They include VirtualMachine objects for the sender and recipient, and an int for the message, which is the sender's logical clock value.
	 */
	private int senderID;
	private int recipientID;
	private int message;
	private final static String delimiter = "\t";
	
	/*
	 * Constructor to take in the parameters and set the fields appropriately
	 */
	public Message(int sender, int recipient, int message) {
		this.senderID = sender;
		this.recipientID = recipient;
		this.message = message;
	}
	
	public int getSenderID() {
		return senderID;
	}
	
	public int getRecipient() {
		return recipientID;
	}
	
	public int getMessage() {
		return message;
	}
	
	public String toString() {
		return senderID + delimiter + recipientID + delimiter + message;
	}
	
	public static Message fromString(String s) {
		String[] split = s.split(delimiter);
		Message m  = new Message(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		return m;
	}
}
