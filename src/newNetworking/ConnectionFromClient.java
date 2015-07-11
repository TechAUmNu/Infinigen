package newNetworking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ConnectionFromClient implements Runnable, ActionListener{
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	NetworkMessage message;
	Client client;
	
	
	ConnectionFromClient(Socket socket) {
		this.socket = socket;		
	}
	
	
	public void run() {
		System.out.println("Connection received from " + socket.getInetAddress().getHostName());
	
		// 3. get Input and Output streams
		try {
			socket.setPerformancePreferences(0, 1, 2);
			socket.setTcpNoDelay(true);
			out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.flush();
	
			in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			
			
			// 4. The two parts communicate via the input and output streams
			do {
				try {
					message = (NetworkMessage) in.readObject();
					
					if (message.disconnect){ //Check if the client is disconnecting
						System.out.println(client.username + " disconnected"); //if they are print their user name
					}else{
						processMessage();	
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			} while (!message.disconnect);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				socket.close();

			} catch (Exception e) {
				// Don't really care the connection is already dead anyway
			}

		}
	}


	private void processMessage() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
				
				
	
}
