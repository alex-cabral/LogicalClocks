import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class VirtualMachine {
	private int id;
	private int clock;
	private String logFile;
	private FileWriter fileWriter;
	private Queue<Message> messageQueue;
	private ObjectOutputStream out;
	private String server;
	private int port;
	private Socket socket;
	
	/**
	 * The constructor for the class
	 * @param 	ticks, the number of ticks per second to control how often to perform actions
	 * @param 	id, the integer id associated with this instance of the Virtual Machine, for logging and message sending
	 */
	public VirtualMachine(String server, int port, int id) {
		this.id = id;
		this.server = server;
		this.port = port;
		try {
			socket = new Socket(server, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clock = 0;
		logFile = "log" + id + ".txt";
		createLogFile(logFile);
		messageQueue = new LinkedList<Message>();
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates a log file for the virtual machine. The file will be unique because the name is based on the unique VM id
	 * This method is only ever called internally
	 * @param 	filename
	 */
	private void createLogFile(String filename) {
		try {
			File f = new File(filename);
			f.createNewFile();
		}
		catch (IOException e) {
			System.out.println("Could not create log file " + e);
		}
	}
	
	/**
	 * Every time the logical clock ticks, the VM should conduct an event
	 * First check for unread messages, if there are some that's the event, otherwise generate a random number
	 */
	public void conductEvent() {
		if (messageQueue.size() > 0) {
			readMessageFromQueue();
		}
		else {
			int r = generateRandomNumber();
			selectEvent(r);
		}
	}
	
	/**
	 * This method provides access for the VM's listening thread to add new messages to the queue as they come in from the server
	 * @param 	m, the message to add to the queue
	 */
	public void addToMessageQueue(Message m) {
		messageQueue.add(m);
	}
	
	/**
	 * This mehthod 
	 */
	private void readMessageFromQueue() {
		Message m = messageQueue.remove();
		int otherClock =  m.getMessage();
		updateLogicalClock(otherClock);
		String event = "RECEIVE FROM: " + m.getSenderID() + " QUEUE SIZE: " + messageQueue.size();
		logEvent(event);
	}
	
	/**
	 * This method updates the time of this VM's logical clock by comparing it to another clock value
	 * Based on the rules of Lamport's logical clock, the clock should be set to the max value of its current time or the message time + 1
	 * This will also work for sending messages and internal events by just passing in the current max value, then adding 1 to that
	 * @param otherClock
	 */
	private void updateLogicalClock(int otherClock) {
		clock =  Math.max(clock,  otherClock + 1);
	}
	
	
	private void selectEvent(int r) {
		String event = "";
		if (r <= 2) { // send one message
			int recipientID = getRecipientID(r);
			sendMessage(recipientID);
			event =  "SEND TO: " + recipientID;
		}
		else if (r == 3) { // send two messages
			int recip1 = getRecipientID(1);
			int recip2 = getRecipientID(2);
			sendMessage(recip1);
			sendMessage(recip2);
			event = "SEND TO: " + recip1 + " AND " + recip2;
		}
		else {
			event = "INTERNAL";
		}
		updateLogicalClock(clock); // pass in current value so it gets updated to that value + 1
		logEvent(event);
	}
	
	/**
	 * 
	 * @param r
	 */
	private int getRecipientID(int r) {
		int recipient = id - r;
		if (recipient <= 0) {
			recipient += 3;
		}
		return recipient;
	}
	
	private void sendMessage(int recipientID) {
		Message m = new Message(id, recipientID, clock);
		try {
			out.writeObject(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method generates a random integer in the range of 1 to 10, inclusive. 
	 * The random number is then used to determine the action the VM should take.
	 * This method will only get called internally so is private
	 * @return	the randomly generated integer
	 */
	private int generateRandomNumber() {
		Random r = new Random();
		return r.nextInt(10) + 1;
	}
	
	public void runVM() {
		Random r = new Random();
		int ticks = r.nextInt(6) + 1;
		long millis = (long) (1.0 / ticks);
		long endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(1L, TimeUnit.MINUTES);
		while (System.nanoTime() < endTime) {
			conductEvent();
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method logs an event to the VM's log file by opening the file in append mode and adding the line to the file
	 * @param event
	 */
	public void logEvent(String event) {
		String eventLog = event + "\t" +  System.nanoTime() + "\t" + clock;
		try {
			fileWriter = new FileWriter(logFile, true); //Set true for append mode
		    PrintWriter printWriter = new PrintWriter(fileWriter);
		    printWriter.println(eventLog);  //New line
		    printWriter.close();
		} catch (IOException e) {
			System.out.println("Error saving " + event + " to " + logFile + ": " + e);
		} 
	}
	
	public static void main(String[] args) {
		if (args.length != 3) { 
			System.err.println("Usage: java Client <host> <port> <vmID>");
			System.exit(1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int vmID = Integer.parseInt(args[2]);
		
		VirtualMachine vm = new VirtualMachine(host, port, vmID);		
		System.out.println("VM" + vmID + " created");
	}
}
