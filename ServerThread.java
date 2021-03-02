import java.io.*;
import java.net.*;
import java.util.ArrayList;


/**
	 * The ClientThread class controls the connection for each client to the server via a Socket.
	 * Each time a new client is connected, a new instance of ClientThread is created.
	 * Because it uses threading, it extends the Thread class.
	 */
	public class ServerThread extends Thread{
		private int id;
		private Socket socket;
		private Server server;
		private PrintWriter writer;
		private ObjectOutputStream out;
		
		
		/** 
		 * The ClientThread constructor, which takes in a Socket and Server
		 * @param 	socket and server this thread connect to
		 */
		public ServerThread(Socket socket, Server server, int id) {
			this.socket = socket;
			this.server = server;
			this.id =  id;
		}

		/**
		 * This method returns the id associated with the virtual machine
		 * It is used for sending messages between the different virtual machines
		 * Because the Thread class has a method called getID(), this must be called something different
		 * @return	id, the id associated with the VM
		 */
		public int getVMID() {
			return id;
		}
		
		/**
		 * This method sends a message to the Virtual Machine to be stored in its message queue.
		 * @param 	message object to be sent
		 */
		public void sendMessage(Message m) {
			try {
				out.writeObject(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		/**
		 * This method runs the thread
		 * It is very long with a lot of logic to handle the different cases from the user
		 */
		public void run() {
			try {
	            //BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // input from client
	            DataInputStream reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);  // output to client
	            writer.println("\n>> Welcome to the chat app! \n");
				
				
			// this code executes when the user enters QUIT or DELETE
				server.removeThread(this);
				socket.close();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

	}