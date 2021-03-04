import java.io.*;
import java.net.*;
import java.util.ArrayList;


/**
	 * The ServerThread class controls the connection for each virtual machine to the server via a Socket.
	 * Each time a new client is connected, a new instance of ServerThread is created.
	 * Because it uses threading, it extends the Thread class.
	 */
	public class ServerThread extends Thread{
		private int id;
		private Socket socket;
		private Server server;
		private ObjectOutputStream out;
		private final int NUMVMS = 3;
		private PrintWriter writer;
		
		/** 
		 * The ClientThread constructor, which takes in a Socket and Server
		 * @param 	socket and server this thread connect to
		 */
		public ServerThread(Socket socket, Server server, int id) {
			this.socket = socket;
			this.server = server;
			this.id =  id;
			System.out.println("ServerThread " +id + " created.");
			//checkNumVMs(id);
		}
		
		/**
		 * This method checks to see if the number of VMs on the server is equal to the number expected to run the program
		 * If so, it sends a message to all of the VMs, so they will start running
		 * @param	num, the number of VMs connected to the server
		 */
//		private void checkNumVMs(int num) {
//			if (num == NUMVMS) {
//				server.startThreads();
//			}
//		}

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
//			try {
//				out.writeObject(m);
//				out.flush();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch (NullPointerException e) {
//				System.out.println("Null pointer writing message: " + id + " "  + e);
//			}
		}
	
		
		/**
		 * This method runs the thread
		 * It is very long with a lot of logic to handle the different cases from the user
		 */
		public void run() {
            try {
            	//ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
				//out = new ObjectOutputStream(socket.getOutputStream());
//				
//				Message start = new Message(-1, id, -1); // now get the VM running by sending a message using -1 to show it's from the server
//				sendMessage(start);
//				out.flush();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // input from client
				writer = new PrintWriter(socket.getOutputStream());
				
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
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
				// this code executes when the user enters QUIT or DELETE
//				server.removeThread(this);
//				socket.close();
		}
}