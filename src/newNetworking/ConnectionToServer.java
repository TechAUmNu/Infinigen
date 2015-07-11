package newNetworking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionToServer implements Runnable, ActionListener{
	
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	Client client;
	NetworkMessage message;
	
	ConnectionToServer(Client c) {
		client = c;
	}
	
	

	public void run() {
		try {
			// 1. creating a socket to connect to the server
			
			System.out.println("about to connect to server");
			socket = new Socket("localhost", 19987);
			socket.setPerformancePreferences(0, 1, 2);
			socket.setTcpNoDelay(true);
			System.out.println("Connected to play2.ghsgaming.com in port 19987");
			// 2. get Input and Output streams
			out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.flush();
			in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			// 3: Communicating with the server
			
			do {
				try {
					// System.out.println("Waiting on message from server");
					message = (NetworkMessage) in.readObject();
					// System.out.println("Message received from server");
					if (!message.disconnect) {
						processMessage();
					}

				} catch (ClassNotFoundException classNot) {
					System.err.println("data received in unknown format");
				} catch (EOFException e) {
					// Ignore this error since it will always occur when you
					// quit.
				}
			} while (!message.disconnect);
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();

		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}



	private void processMessage() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
			
			
}
