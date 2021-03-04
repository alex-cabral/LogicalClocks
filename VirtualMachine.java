/**
 * This class includes all of the state and functionality for the Virtual Machine
 * It includes a main method to run everything when a new instance of the class is created
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class VirtualMachine {
	/**
	 * The fields of the VirtualMachine include a unique id, logical clock, components for connection
	 */
	private int id;
	private int clock;
	private String logFile;
	private FileWriter fileWriter;
	private Queue<Message> messageQueue;
	private String server;
	private int port;
	private Socket socket;	
	private int ticks;
	private PrintWriter writer;
	private ListenerThread listener;
	
	/**
	 * The constructor for the class, which sets the fields, creates the log file, and creates the connection to the server
	 * @param 	ticks, the number of ticks per second to control how often to perform actions
	 * @param 	id, the integer id associated with this instance of the Virtual Machine, for logging and message sending
	 */
	public VirtualMachine(String server, int port, int id) {
		this.id = id;
		this.server = server;
		this.port = port;
		clock = 0;
		logFile = "log" + id + ".txt";
		createLogFile(logFile);
		ticks = generateRandomNumber(6);
		messageQueue = new LinkedList<Message>();
		connectToServer(server, port);
		logEvent("TICKS=" +ticks);
	}
	
	/**
	 * This method handles all of the connection between the VM and the server, including the socket, PrintWriter to send to the server
	 * and creation of a separate ListenerThread to receive messages from the server
	 * @param server
	 * @param port
	 */
	private void connectToServer(String server, int port) {
		try {
			socket = new Socket(server, port);
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			listener = new ListenerThread(socket, this); 
	        listener.start(); // handle listening from the server
	        
		} catch (UnknownHostException e) {
			System.out.println("Cannot connect to host " + e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method creates a log file for the virtual machine. The file will be unique because the name is based on the unique VM id
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
	private void conductEvent() {
		if (messageQueue.size() > 0) {
			readMessageFromQueue();
		}
		else {
			int r = generateRandomNumber(10); // here we changed the value to check different internal event probabilities
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
	 * This method reads from the message queue, sets the updated clock time, then logs the event
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
	
	
	/**
	 * This method determines the event based on the random number generated
	 * The methods are linked to the numbers as specified in the assignment
	 * A value of 1 or 2 sends a message to a single VM, a value of 3 sends to both,
	 * and any other value results in an internal event
	 * @param r
	 */
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
	 * This method determines which VM to send a message to based on the random number and VM ID values
	 * @param r
	 */
	private int getRecipientID(int r) {
		int recipient = id - r;
		if (recipient < 0) {
			recipient += 3;
		}
		return recipient;
	}
	
	/**
	 * This method uses the PrintWriter to send a message to another recipient.
	 * @param recipientID
	 */
	private void sendMessage(int recipientID) {
		Message m = new Message(id, recipientID, clock);
		writer.println(m.toString());
		writer.flush(); // need to flush the writer each time
	}
	
	/**
	 * This method generates a random integer in the range of 1 to 10, inclusive. 
	 * The random number is then used to determine the action the VM should take.
	 * This method will only get called internally so is private.
	 * To test lower probability of internal event and clock variation, we changed the max random number passed in here
	 * @return	the randomly generated integer
	 */
	private int generateRandomNumber(int n) {
		Random r = new Random();
		return r.nextInt(n) + 1;
	}
	
	
	/**
	 * This method runs the VM logic
	 * It sets a delay to the ticks per second, in milliseconds, and conducts events for one minute
	 * This method uses System.nanoTime() because it is supposed to be more stable than other methods
	 */
	public void runVM() {
		System.out.println("Running VM " + id);
		long delay = (long) (1000.0 / ticks); // divide 1 second by number of ticks per second and that's the delay
		long endTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(1L, TimeUnit.MINUTES);
		
		// run for one minute
		while (System.nanoTime() < endTime) {
			conductEvent();
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("VM " + id + " done.");
		
		// then close all of the connections
		try { 
			writer.close();
			listener.stopRunning();
			socket.close();
			
		} catch (IOException e) {
			// do nothing
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
	
	/**
	 * This method runs every time a new VirtualMachine is created
	 * It sets the host, port, and VM id from the command line and runs the VM
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) { 
			System.err.println("Usage: java Client <host> <port> <id>");
			System.exit(1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int id = Integer.parseInt(args[2]);
		
		VirtualMachine vm = new VirtualMachine(host, port, id);
		vm.runVM();
	}
}
