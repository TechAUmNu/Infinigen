package main.java.com.ionsystems.infinigen.newNetworking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedTransferQueue;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.messages.Messaging;
import main.java.com.ionsystems.infinigen.messages.Tag;

public class ClientNetworkAdapter implements Runnable {

	private Socket socket;

	private ObjectOutputStream out;
	private ObjectInputStream in;
	

	
	private NetworkMessage inMessage;
	Ack ack;
	private Client client;
	private int direction;

	private Tag tag;

	/**
	 * Used for send
	 * @param socket
	 * @param direction
	 * @param tag
	 */
	public ClientNetworkAdapter(Socket socket, Tag tag) {
		this.socket = socket;
		this.direction = 1;
		this.tag = tag;
	}

	/**
	 * Used for receive
	 * @param socket
	 * @param direction
	 */
	public ClientNetworkAdapter(Socket socket) {
		this.socket = socket;
		this.direction = 0;
	}

	@Override
	public void run() {
		if (direction == 0) { // Receiving
			recieve();
		} else {
			send(); // Sending
		}

	}

	/**
	 * This will only deal with the actual transfer of data between the client and server. No processing should occur in this thread. Receive is only receiving
	 * data from the server and sending an acknowledge back.
	 */
	public void recieve() {
		System.out.println("Connected to " + socket.getInetAddress().getHostName());

		try {
			System.out.println("Creating output stream");
			//out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(socket.getOutputStream(), 4096, true)));
			out.flush();

			//in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));

			/**
			 * This do loop runs until the server disconnects
			 */
			do {
				try {
					
					inMessage = (NetworkMessage) in.readObject();
					
					if(inMessage != null){
						if (inMessage.disconnect) {
							System.out.println("Server disconnected");
						} else {
							processMessage(inMessage); // add the message from the
														// server to the incoming
														// queue
							sendAck(false); // send an acknowledgement to the server
											// saying there was no error
						}
					}else{
						inMessage = new NetworkMessage();
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
					sendAck(true); // send an acknowledgement to the server
									// saying there was an error
				} catch (SocketException connectionReset) {
					System.err.println("server closed the connection");
					break;
				}
			} while (!inMessage.disconnect);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				socket.close();
				System.out.println("server disconnected");

			} catch (Exception e) {
				System.err.println("A problem occured while closing the connection: " + e.getMessage());
			}

		}
	}

	public void send() {
		System.out.println("Connected to " + socket.getInetAddress().getHostName());

		try {
			System.out.println("Creating output stream");

			//out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(socket.getOutputStream(), 4096, true)));
			out.flush();

			//in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));

			/**
			 * This do loop runs until the server disconnects
			 */
			do {
				try {
					
					sendMessage(); // Send a message to the server

					// Wait for an acknowledgement
					ack = (Ack) in.readObject();
					System.out.println("ack recieved from server");
					if (!ack.ack) {
						System.out.println("Problem with message");
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");					
				} catch (SocketException connectionReset) {
					System.err.println("server closed the connection");
					break;
				}
			} while (!ack.disconnect);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				socket.close();
				System.out.println("server disconnected");

			} catch (Exception e) {
				System.err.println("A problem occured while closing the connection: " + e.getMessage());
			}

		}
	}

	private void sendAck(boolean error) {
		Ack ack = new Ack();
		ack.ack = !error;
		sendNetworkMessage(ack);
	}

	private void processMessage(NetworkMessage msg) {
		
		System.out.println("Message recieved from server: " + msg.tag);
		Messaging.addMessage(msg.tag, msg);
	}
	
	private void sendMessage(){
		NetworkMessage msg = (NetworkMessage) Messaging.takeLatestMessage(tag);
		if(msg != null)
			msg.client = Globals.getClient();
			System.out.println("Sending message to server: " + msg.tag.toString());
			sendNetworkMessage(msg);
	}
	
	private void sendNetworkMessage(Object o){
		try {			
			out.writeObject(o);
			out.flush();
			out.reset();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			System.exit(0);
		}
	}


	
}
