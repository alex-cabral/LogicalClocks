/**
 * This class stores the state and functionality for a message sent between users.
 * The main purpose for this class is the getter methods, so that it's easy to see if a user has unread messages.
 */
public class Message {
	/**
	 * The fields of a message. They include VirtualMachine objects for the sender and recipient, and an int for the message, which is the sender's logical clock value.
	 */
	private VirtualMachine sender;
	private VirtualMachine recipient;
	private int message;
	private final String delimiter = "-|::|-";
	
	/*
	 * Constructor to take in the parameters and set the fields appropriately
	 */
	public Message(VirtualMachine sender, VirtualMachine recipient, int message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
	}
	
	public VirtualMachine getSender() {
		return sender;
	}
	
	public VirtualMachine getRecipient() {
		return recipient;
	}
	
	public int getMessage() {
		return message;
	}
	
	public String toString() {
		return sender + delimiter + recipient + delimiter + message;
	}
}
