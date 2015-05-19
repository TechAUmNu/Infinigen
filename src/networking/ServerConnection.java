package networking;

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
import java.util.ArrayList;

import javax.swing.Timer;

import sprites.UnitSprite;
import dataStuctures.Client;
import dataStuctures.ClientCommand;
import dataStuctures.InterthreadHolder;
import dataStuctures.Message;

//A connection from a client to the server
public class ServerConnection implements Runnable, ActionListener{
	Socket requestSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	long timeTaken;
	Client client;
	private boolean disconnect;
	
	

	public ServerConnection(Client c) {
		client = c;
	}

	public void run() {
		try {
			// 1. creating a socket to connect to the server

			System.out.println("about to connect to server");
			requestSocket = new Socket("localhost", 19987);
			requestSocket.setPerformancePreferences(2, 2, 1);
			requestSocket.setTcpNoDelay(true);
			System.out.println("Connected to play2.ghsgaming.com in port 19987");
			// 2. get Input and Output streams
			out = new ObjectOutputStream(new BufferedOutputStream(requestSocket.getOutputStream()));
			out.flush();
			in = new ObjectInputStream(new BufferedInputStream(requestSocket.getInputStream()));
			// 3: Communicating with the server
			
			//Timer which fires the send message event, this should be on a fast time since we want to send the message as soon as possible.
			Timer timer = new Timer(5, this); //With 5ms it will send the message less than 5ms after it was created. Checking for messages 200 times a second.
			timer.start(); 
			
			
			do {
				try {
					//System.out.println("Waiting on message from server");
					Message message = (Message) in.readObject();
					//System.out.println("Message recieved from server");
						if (!message.disconnect) {

						// Update the current world if it isn't null
						
						if (message.world != null) {
							//System.out.println(message.world.getSprites().get(0).getX());
							System.out.println("updating to server world");
							long totalTime = System.currentTimeMillis() - timeTaken;
							System.out.println("Round trip + transfer time: "+ totalTime);
							InterthreadHolder.getInstance().buildWorldFromNetwork(message.world);
							
							if(message.walkability != null && !message.walkability.equals(null)){
								System.out.println("updating walkability");
								InterthreadHolder.getInstance().setWalkability(message.walkability);
							}
							//System.out.println("World updated to server version");
							//System.out.println("displaying coordinates of sprite 1");
							//System.out.println(InterthreadHolder.getInstance().getWorld().sprites.get(0).getX());
						}
						if(message.sendClient){
							//This will happen when the server asks for a client info update.
							 Message m = new Message(null,false,null,true, null, true, null);
							 //Set the client username and teamID, the server will fill in the IP address.
							 m.SetClient(client);
							 //Send the message
							 sendMessage(m);
						}
						if(message.status != null){
							InterthreadHolder.getInstance().setStatusText(message.status);
							if(message.status.equals("done")) InterthreadHolder.getInstance().setLoading(false);
						}
						if(message.command != null){
							ProcessInput(message);
						}
						if(message.disconnect){
							//The disconnect flag on the message is set, so set the disconnect flag to close the connection.
							disconnect = true;
						}
						//Clear the message so it is always null before receiving one.
						message = null;
						
					        
						
					}

				} catch (ClassNotFoundException classNot) {
					System.err.println("data received in unknown format");
				}catch (EOFException e) {
					//Ignore this error since it will always occur when you quit.
				}
			} while (!disconnect);
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		
	
		} finally {
			// 4: Closing connection
			try {
				in.close();
				out.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	
	
	//sends the waiting messages to the server
	void sendMessages(){
		//System.out.println("Starting sending messages to server");
		
		for (Message m : InterthreadHolder.getInstance().getMessages()) {
			sendMessage(m);
			if(m.disconnect){
				System.exit(0);
			}
			//System.out.println("Sending message to server: " + m.command.commandType);
		}
		//System.out.println("Finished sending messages to server");
		InterthreadHolder.getInstance().clearMessages();	
		
		
		processUnsent();
		
		
	}

	private void processUnsent() {
		// Loop through messages waiting to be sent to the
		// server
		 if (InterthreadHolder.getInstance().isServerReconciliation()) {
	          // Server Reconciliation. Re-apply all the inputs not yet processed by
	          // the server.
			 ArrayList<Message> remove = new ArrayList<Message>();
	          for(Message m : InterthreadHolder.getInstance().getPendingMessages()){
	        	  if(m.command != null){
	        	
	        	  
	            if (m.stateID.lessThan(InterthreadHolder.getInstance().getLastProcessedInput()) || m.processed) {
	              // Already processed. Its effect is already taken into account
	              // into the world update we just got, so we can drop it.
	            	remove.add(m);
	            } else {
	              // Not processed by the server yet. Re-apply it.
	            	ProcessInput(m);
	              
					}
				}
			}
	          //Remove all processed inputs
	          InterthreadHolder.getInstance().getPendingMessages().removeAll(remove);
		 }
	}
	
	void sendMessage(Message msg) {
		try {
			out.writeObject(msg);
			out.flush();
			out.reset();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		sendMessages();
	}
	
	
	// Process the client command that was received, we MUST process the commands client side as well, if not then we will have serious jumpiness problems!
	//It is important the processing code on the client always applies any command from the server as it will be from other clients selecting things. 
	//Since we use TCP this "should" always be in order.
	//TODO: A check will probably be required to make sure that we are not desynced, if we are then we request an update of the sprite positions from the server.
		private void ProcessInput(Message m) {
			
			//Since we are applying all selects de-selects etc, we need to only show ones relating to our client on the UI otherwise it will get VERY confusing!
			switch (m.command.commandType) {
			case "Select":
				System.out.println("Client Processed Select order");
				for (UnitSprite s : InterthreadHolder.getInstance().getWorld()
						.getPlayerSprites(InterthreadHolder.getInstance().getClient())) {
					for (UnitSprite sp : m.command.selectedUnits) {
						if (s.spriteID == sp.spriteID) {
							s.setSelected(true, InterthreadHolder.getInstance().getClient().teamID);
							System.out.println("Selecting unit:" + s.spriteID);
						}
					}
				}
				break;
			case "deSelectAll":
				System.out.println("Client Processed deSelectAll order");
				for (UnitSprite s : InterthreadHolder.getInstance().getWorld()
						.getPlayerSprites(InterthreadHolder.getInstance().getClient())) {
					s.setSelected(false, InterthreadHolder.getInstance().getClient().teamID);
					System.out.println("Deselecting unit:" + s.spriteID);
				}
				break;
			case "Move":
				System.out.println("Client Processed send move order");
				for (UnitSprite s : InterthreadHolder.getInstance().getWorld()
						.getPlayerSprites(InterthreadHolder.getInstance().getClient())) {
					if (s.isSelectedByPlayer(InterthreadHolder.getInstance().getClient())) {
						s.moveTo(m.command.x + m.command.camX, m.command.y + m.command.camY);
						System.out.println("Moving unit:" + s.spriteID);
					}
				}
				break;
			}
			if(m.client.username.equals(client.username)){
				InterthreadHolder.getInstance().setLastProcessedInput(m.stateID);
				m.processed = true;
			}
		}
}
