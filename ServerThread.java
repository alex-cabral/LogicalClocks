import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;


/**
	 * The ServerThread class controls the connection for each virtual machine to the server via a Socket.
	 * Each time a new client is connected, a new instance of ServerThread is created.
	 * Because it uses threading, it extends the Thread class.
	 */
	public class ServerThread extends Thread{
		private int id;
		private Socket socket;
		private Server server;
		private PrintWriter writer;
		
		/** 
		 * The ServerThread constructor, which takes in a Socket, Server, and id to tie it to the VM
		 * @param 	socket and server this thread connect to
		 */
		public ServerThread(Socket socket, Server server, int id) {
			this.socket = socket;
			this.server = server;
			this.id =  id;
			System.out.println("ServerThread " +id + " created.");
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
			writer.println(m.toString());
		}
		
		/**
		 * This method runs the thread to listen to messages from VMs and direct them appropriately
		 * The catch statements are all empty to hide exceptions that bubble up when the VM disconnects from the server.
		 */
		public void run() {
            try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // input from client
				writer = new PrintWriter(socket.getOutputStream(), true);
				
            	while (true) {
    				try {
    					String s = reader.readLine();
    					Message m = Message.fromString(s);
       					server.sendMessage(m);
    				} 
    				catch (EOFException e) {
    					// do nothing
    				}
    				catch (Exception e) {
    					// do nothing
    				}
            	}
            }
			 catch (IOException e) {
				// do nothing
			}
		}
}