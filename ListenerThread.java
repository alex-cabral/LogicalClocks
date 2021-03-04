/**
 * This class creates a thread for the virtual machine to use to receive messages from the server.
 * The thread helps it so that the VM can receive messages from the server while also sending to other VMs at the same time.
 */

import java.io.*;
import java.net.*;

public class ListenerThread extends Thread implements Runnable {
	/**
	 * The fields of the ListenerThread include components to connect to and communicate with the server
	 */
	private Socket socket;
	private boolean running = true;
	private VirtualMachine vm;
	private BufferedReader in;
	
	/**
	 * Constructor to set the fields
	 * @param socket
	 * @param vm
	 */
	public ListenerThread(Socket socket, VirtualMachine vm){
		this.socket = socket;
		this.vm = vm;
	}
	
	/**
	 * This method sets the boolean running to false so that the thread will stop running
	 * This is only called when the VM is done running
	 */
	public void stopRunning() {
		running = false;
	}
	
	/**
	 * This method overrides the one from the Runnable interface (as it must)
	 * In this method, we simply loop infinitely reading in messages from the server as they come in and adding them to the VMs queue.
	 */
	@Override
	public void run() {
		try{
			Message message = null;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(running){
				try {
					String s = in.readLine();
					message = Message.fromString(s); // convert to Message object
					vm.addToMessageQueue(message); 
				} catch (Exception e) {
					// do nothing
				}
			}
			socket.close();
		}
		catch(Exception e){
			System.err.println("Error receiving message from server.");
		}
	}
}
