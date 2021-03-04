/**
 * This class creates a thread for the virtual machine to use to receive messages from the server.
 * The thread helps it so that the VM can receive messages from the server while also sending to other VMs at the same time.
 */

import java.io.*;
import java.net.*;

public class ListenerThread extends Thread implements Runnable {
	private Socket socket;
	//private ObjectInputStream in;
	private boolean running = true;
	private VirtualMachine vm;
	private BufferedReader in;
	
	public ListenerThread(Socket socket, VirtualMachine vm){
		this.socket = socket;
		this.vm = vm;
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
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(running){
				try {
					String s = in.readLine();
					//String[] components = s.split(" ");
					message = Message.fromString(s);
					//message = new Message(Integer.parseInt(components[0]), Integer.parseInt(components[1]), Integer.parseInt(components[2]));
					vm.addToMessageQueue(message); // has to be a Message object
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
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
