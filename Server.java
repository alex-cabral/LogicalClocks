import java.io.*;
import java.util.*;
import java.net.*;

/** 
 * The Server class stores all of the state and functionality of the server.
 * This class can be run from the console, and should generally be run before the client class to ensure that the connection works.
 * Only one instance of the Server class is needed, even with multiple clients.
 * It contains a nested class, ServerThread, which handles all of the threading for multiple clients.
 */
public class Server {
	
	/** 
	 * The fields for the ChatServer class
	 */
	private int port;
	private ArrayList<ServerThread> activeThreads;
	private boolean running;
	
	/**
	 * The constructor for the Server class sets the port as specified by the user 
	 * and initializes an array list to store the client threads.
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
				thread.start();
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
	 * This method sends a message to a user who is in the database
	 * @param 	message, the message to be sent
	 */
	public void sendMessage(Message m) {
		int recipient = m.getRecipient();
		ServerThread thread = activeThreads.get(recipient);
		thread.sendMessage(m);
	}
	
	/**
	 * The main method is run immediately when the Server class is run from the console.
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
		
		Server server = new Server(port);
		server.start();
	}
	
}
