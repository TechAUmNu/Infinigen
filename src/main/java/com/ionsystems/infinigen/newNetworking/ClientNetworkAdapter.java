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

public class ClientNetworkAdapter implements Runnable {

	private Socket socket;

	private ObjectOutputStream out;
	private ObjectInputStream in;
	LinkedTransferQueue<Object> inQueue;

	private LinkedTransferQueue<Object> outQueue;
	private NetworkMessage inMessage;
	Ack ack;
	private Client client;
	private int direction;

	public ClientNetworkAdapter(Socket socket, int direction, LinkedTransferQueue<Object> bandwidthRecieveQueue) {
		this.socket = socket;
		this.direction = direction;
		if (direction == 0) { // Receiving
			inQueue = bandwidthRecieveQueue;
		} else {
			outQueue = bandwidthRecieveQueue;
		}
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

			out = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(socket.getOutputStream(), 4096, true)));
			out.flush();

			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));

			/**
			 * This do loop runs until the server disconnects
			 */
			do {
				try {
					System.out.println("Waiting for message from server");
					inMessage = (NetworkMessage) in.readObject();
					System.out.println("Message recieved from server");
					if (inMessage.disconnect) {
						System.out.println("Server disconnected");
					} else {
						processMessage(inMessage); // add the message from the
													// server to the incoming
													// queue
						sendAck(false); // send an acknowledgement to the server
										// saying there was no error
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

			out = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(socket.getOutputStream(), 4096, true)));
			out.flush();

			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));

			/**
			 * This do loop runs until the server disconnects
			 */
			do {
				try {
					System.out.println("sending message to server");
					sendMessage(); // Send a message to the server

					// Wait for an acknowledgement
					Ack ack = (Ack) in.readObject();
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
		inQueue.put(msg);
	}
	
	private void sendMessage(){
		NetworkMessage msg = (NetworkMessage) outQueue.poll();
		if(msg != null)
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
