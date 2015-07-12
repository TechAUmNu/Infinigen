package newNetworking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import newMain.Globals;
import newWorld.Chunk;


public class ConnectionFromClient implements Runnable, ActionListener{
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	NetworkMessage inMessage, outMessage;
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
					System.out.println("Waiting for message from client");
					inMessage = (NetworkMessage) in.readObject();
					System.out.println("Message recieved from client");
					if (inMessage.disconnect){ //Check if the client is disconnecting
						
					}else{
						processMessage();	
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				} catch (SocketException connectionReset){
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
				System.out.println(client.username + " disconnected"); //if they are print their user name
			} catch (Exception e) {
				System.err.println("A problem occured while closing the connection: " + e.getMessage());
			}

		}
	}


	private void processMessage() {
		if(inMessage.client != null){ //Update client info
			System.out.println("Client info recieved updating...");
			client = inMessage.client;
		}
		
		if(inMessage.chunkUpdate){ 
			System.out.println(client.username + " requested chunk update");
			System.out.println("Waiting until loading complete...");
			while(Globals.loading()){
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			chunkUpdate();	
		}
		
		
	}


	private void chunkUpdate() {
		//Client requested a chunk update
			//So we need to send back all the chunks (Loading world)
			
			//First we will send a message with the number of chunks that are to be loaded so the client can make a progress bar for download progress
			
			outMessage = new NetworkMessage();
			outMessage.chunkUpdate = true; // We set this so the client knows it is part of the chunk update			
			outMessage.chunkCount = Globals.getLoadedChunks().size();
			System.out.println("Sending chunk count: " + outMessage.chunkCount);
			sendMessage();
			
			//Next we need to send all the chunks one by one so the client can easily update the progress of the download.
			System.out.println("Sending chunk data");
			for(Chunk c : Globals.getLoadedChunks()){
				outMessage = new NetworkMessage();
				outMessage.chunkUpdate = true;
				outMessage.chunkData = c.getData();
				sendMessage();
			}
			
			//Now the client has all the chunk data we can send a message to say the chunk update has finished.
			System.out.println("Sending complete message");
			outMessage = new NetworkMessage();
			outMessage.chunkUpdate = true;
			outMessage.chunkUpdateComplete = true;
			sendMessage();
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
				
				
	
	void sendMessage() {
		try {
			out.writeObject(outMessage);
			out.flush();
			out.reset();
			outMessage = null;
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
}
