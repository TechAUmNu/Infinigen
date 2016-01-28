package main.java.com.ionsystems.infinigen.newNetworking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.Timer;

import main.java.com.ionsystems.infinigen.global.Globals;

public class ServerNetworkAdapter implements Runnable, ActionListener {
	
	private Socket socket;
	private GZIPOutputStream gzipOut;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Timer timer;
	private NetworkMessage inMessage;
	private Client client;
	
	public ServerNetworkAdapter(Socket socket, int direction){
		this.socket = socket;
	}

	@Override
	public void run() {
		recieve();		
	}
	
	
	public void recieve(){
		System.out.println("Connection received from " + socket.getInetAddress().getHostName());

		
		try {
			System.out.println("Creating output stream");
			gzipOut = new GZIPOutputStream(socket.getOutputStream(), 4096, true);
			out = new ObjectOutputStream(new BufferedOutputStream(gzipOut));
			out.flush();

			
			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));
			

			timer = new Timer(1000, this);
			timer.start(); // Timer for regular updates to client (100ms)

			// 4. The two parts communicate via the input and output streams
			do {
				try {
					// System.out.println("Waiting for message from client");
					inMessage = (NetworkMessage) in.readObject();
					// System.out.println("Message recieved from client");
					if (inMessage.disconnect) { // Check if the client is
												// disconnecting

					} else {
						processMessage();
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				} catch (SocketException connectionReset) {
					System.err.println("Client closed the connection");
					inMessage = new NetworkMessage();
					inMessage.disconnect = true;
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
				timer.stop();
				System.out.println(client.username + " disconnected"); // if
																		// they
																		// are
																		// print
																		// their
																		// user
																		// name

			} catch (Exception e) {
				System.err.println("A problem occured while closing the connection: " + e.getMessage());
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
