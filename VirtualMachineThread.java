/**
 * This class creates a thread for the client to use to receive messages from the server.
 * The thread helps it so that the client can both read from and write to the server at the same time.
 * We got the inspiration for this code from this site:
 * https://stackoverflow.com/questions/28924942/how-to-read-input-from-a-socket-using-threads-java
 */

import java.io.*;
import java.net.*;

public class VirtualMachineThread extends Thread implements Runnable {
	private Socket socket;
	private ObjectInputStream in;
	private boolean running = true;
	private VirtualMachine vm;
	
	public VirtualMachineThread(Socket socket, VirtualMachine vm){
		this.socket = socket;
		this.vm = vm;
    
		try{
			in = new ObjectInputStream((socket.getInputStream())); // get input from the server
		}
		catch(IOException e){
			System.err.println("Error created a buffered reader: " + e);
		}
	}
	
	/**
	 * This method sets the boolean running to false so that the thread will stop running
	 * This is only called when the user enters QUIT
	 */
	public void stopRunning() {
		running = false;
	}
	
	/**
	 * This method overrides the one from the Runnable interface (as it must)
	 * In this method, we simply loop infinitely reading in messages from the server as they come in and outputting them to the client.
	 */
	@Override
	public void run() {
		try{
			Message message = null;
			while(running){
				try {
					message = (Message) in.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				vm.addToMessageQueue(message); // has to be a Message object
			}
		}
		catch(IOException e){
			System.err.println("Error receiving message from server: " + e);
		}

	}
}
