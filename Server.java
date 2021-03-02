import java.io.*;
import java.util.*;
import java.net.*;

/** 
 * The Server class stores all of the state and functionality of the server.
 * This class can be run from the console, and should generally be run before the client class to ensure that the connection works.
 * Only one instance of the Server class is needed, even with multiple clients.
 * It contains a nested class, ClientThread, which handles all of the threading for multiple clients.
 */
public class Server {
	
	/** 
	 * The fields for the ChatServer class
	 */
	private int port;
	private ArrayList<ServerThread> activeThreads;
	private ArrayList<Message> unreadMessages;
	private boolean running;
	private final String delimiter = "-|::|-";
	
	/**
	 * The constructor for the ChatServer class sets the port as specified by the user 
	 * and initializes an array list to store the client threads.
	 * It also populates the array list of usernames that already exist in the app so that duplicates are not created
	 * Finally, it initializes a new Array List to store the usernames of people logged in.  Initially that is empty.
	 * @param port
	 */
	public Server(int port) {
		this.port = port;
		this.activeThreads = new ArrayList<ServerThread>();
	}
	
	
	/**
	 * This method starts the server and keeps it running through an infinite loop to allow new connections from multiple clients.
	 * It uses a ServerSocket to wait for new connections, then a Socket for the client connections.
	 */
	public void start() {
		running = true; // need this boolean to not have an infinite loop so the ServerSocket can be closed
		try {
			ServerSocket serverSocket = new ServerSocket(port); // start the ServerSocket at the port
			// keep the server running for new connections
			while (running) {
				System.out.println("Server waiting on port : " + port); // for debugging purposes
				Socket socket = serverSocket.accept(); // accept new connection from client
				ServerThread thread = new ServerThread(socket, this, activeThreads.size()); // start a new thread on the client socket, set id of the thread to size of threads now
				activeThreads.add(thread); // add to the list of active threads
				thread.start(); // start the thread
			} 
			// this next try/catch took a while to figure out but is needed to actually close the ServerSocket and avoid resource leak
			try { 
				serverSocket.close();
			}
			catch (IOException e) {
				System.out.println("Error closing ServerSocket: " + e);
			}

		} catch (IOException e) {
			System.out.println("Error creating new ServerSocket: " + e); // add where this is happening for debugging
		}
	}
	
	/**
	 * This method removes a thread when a user chooses to quit the program.
	 * @param	the ServerThread to remove
	 */
	public void removeThread(ServerThread thread) {
		activeThreads.remove(thread);
	}
	
	/**
	 * This method checks for any unread messages when a user logs in or deletes their account
	 * @param username
	 */
	public boolean checkForUnreadMessages(String username) {
		int count = 0;
		for (int i = 0; i < unreadMessages.size(); i++) {
			Message m = unreadMessages.get(i);
			if (m.getRecipient().equals(username)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method returns all of the unread messages for a specified user.
	 * Because the messages are added to the end of the ArrayList when they are sent, 
	 * then read from the ArrayList starting at the beginning, they should be in chronological order.
	 * @param 	username searching for unread messages
	 * @return	the ArrayList of unread messages
	 */
	public ArrayList<Message> getUnreadMessages(String username) {
		ArrayList<Message> unreads = new ArrayList<Message>();
		for (int i = 0; i < unreadMessages.size(); i++) {
			Message m = unreadMessages.get(i);
			if (m.getRecipient().equals(username)) {
				unreads.add(m);
				unreadMessages.remove(i); // then remove that message from the arraylist and decrement i because the values have shifted
				i--;
			}
		}
		/**
		 * Then make sure the unreadMessages file is updated as well
		 * Ideally this code would not be here so that the user can get their unread messages quickly, 
		 * But in a non persisting server, we want to ensure the information is updated immediately.
		 */
		ArrayList<String> unreadStrings = new ArrayList<String>();
		for (Message m : unreadMessages) {
			unreadStrings.add(m.toString());
		}
		return unreads;
	}
	
	/**
	 * This method opens a file and adds a line to the end of it
	 * It is used to store new usernames when an account is created and to store unsent messages
	 * @param 	line, the line to add to the file
	 * @param 	filename, the file to add the line to
	 */
	public void addLineToFile(String line, String filename) {
	    FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(filename, true); //Set true for append mode
		    PrintWriter printWriter = new PrintWriter(fileWriter);
		    printWriter.println(line);  //New line
		    printWriter.close();
		} catch (IOException e) {
			System.out.println("Error saving " + line + " to " + filename + ": " + e);
		} 
	}
	
	/**
	 * This method sends a message to a user who is in the database
	 * @param 	sender, who sent the message
	 * @param 	recipient, who is supposed to receive the message
	 * @param 	message, the text to be sent
	 * @return	boolean, true if the user is online and false if not to alert the recipient.
	 */
	public boolean sendMessage(String sender, String recipient, String message) {
		ServerThread thread = checkThreads(recipient.toLowerCase());
		if (thread != null) { // if  the user is online
			thread.sendMessage(sender, message);
			return true;
		}
		else { // otherwise store it in unread messages so it can be sent later when the user logs in
			Message m = new Message(sender, recipient, message.replaceAll("\n", ""));
			unreadMessages.add(m);
			String messageString = sender + delimiter + recipient + delimiter + message; // use format dictated for the file
			addLineToFile(messageString, messageFile);
			return false;
		}
	}
	
	/**
	 * The main method is run immediately when the ChatServer class is run from the console.
	 * It starts the server at the port specified by the user (or the default if none is specified).
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		int port = 0;
		
		try {
			port = Integer.parseInt(args[0]);
		}
			
		catch (NumberFormatException e) {
			System.err.println("Enter an integer port number. Usage is: java Server <port>");
			System.exit(1);
		}
		
		// might want to add a try/catch around this
		Server server = new Server(port);
		server.start();
	}
	
}
