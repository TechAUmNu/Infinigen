package main.java.com.ionsystems.infinigen.networking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.Timer;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.world.Chunk;


//For physics updates we will run a timer on the server that sends out the updated physics data every 1/10 of a second.


public class ConnectionFromClient implements Runnable, ActionListener{
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	GZIPOutputStream gzipOut;
	NetworkMessage inMessage;
	Client client;
	int physicsUpdateCount = 0;
	Timer timer;
	
	CopyOnWriteArrayList<NetworkMessage> sendQueue = new CopyOnWriteArrayList<NetworkMessage>();
	
	
	ConnectionFromClient(Socket socket) {
		this.socket = socket;		
	}
	
	
	public void run() {
		System.out.println("Connection received from " + socket.getInetAddress().getHostName());
	
		// 3. get Input and Output streams
		try {
			socket.setPerformancePreferences(0, 1, 2);
			socket.setTcpNoDelay(true);
			System.out.println("Creating output stream");
			gzipOut = new GZIPOutputStream(socket.getOutputStream(), 4096, true);
			out = new ObjectOutputStream(new BufferedOutputStream(gzipOut));
			out.flush();
			
			NetworkMessage outMessage = new NetworkMessage();
			outMessage.clientIDChange = true; // We set this so the client knows it is part of the chunk update			
			outMessage.clientID = Globals.getClientID();
			System.out.println("Sending client id: " + outMessage.clientID);
			queueMessage(outMessage);
			
			System.out.println("Creating input stream");
			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));
			System.out.println("Ready");
			
			
			timer = new Timer(1000, this); timer.start(); //Timer for regular updates to client (100ms)
			
			// 4. The two parts communicate via the input and output streams
			do {
				try {
					//System.out.println("Waiting for message from client");
					inMessage = (NetworkMessage) in.readObject();
					//System.out.println("Message recieved from client");
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
				timer.stop();
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
		
		if(inMessage.newEntity){
			newObject();
		}
		
		
	}


	private void newObject() {
		// Here we will add the new rigid body to the physics simulation and forward the message onto the other clients
		
		//We want to get the message to the other clients ASAP so we queue it first
		//queueMessage(inMessage);
		
		//Now we deal with the entities
		
		for(PhysicsEntity e : inMessage.entityData){
			System.out.println(e.getBody().getWorldTransform(new Transform()).origin);
			Globals.getPhysics().getProcessor().addPhysicsEntity(e); //Thats easy :D
			Globals.addEntity(e, true); //This is for new client joining after game start so we can send them everything.
		}
		
		
		
		//It can't be that simple ?? Well ok then! (SIMPLE BUT STUPIDLY SLOW :D)
		
		
	}


	private void chunkUpdate() {
		//Client requested a chunk update
			//So we need to send back all the chunks (Loading world)
			
			//First we will send a message with the number of chunks that are to be loaded so the client can make a progress bar for download progress
			
			NetworkMessage outMessage = new NetworkMessage();
			outMessage.chunkUpdate = true; // We set this so the client knows it is part of the chunk update			
			outMessage.chunkCount = Globals.getLoadedChunks().size();
			//System.out.println("Sending chunk count: " + outMessage.chunkCount);
			queueMessage(outMessage);
			
			//Next we need to send all the chunks one by one so the client can easily update the progress of the download.
			//System.out.println("Sending chunk data");
			for(Chunk c : Globals.getLoadedChunks()){
				outMessage = new NetworkMessage();
				outMessage.chunkUpdate = true;
				outMessage.chunkData = c.getData();
				queueMessage(outMessage);
			}
			
			//Now the client has all the chunk data we can send a message to say the chunk update has finished.
			//System.out.println("Sending complete message");
			outMessage = new NetworkMessage();
			outMessage.chunkUpdate = true;
			outMessage.chunkUpdateComplete = true;
			queueMessage(outMessage);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		//This is called every 100ms and is used for sending updates of current game state to the client
		
		//So we want to send the physics data to the client
		
		
		//First we need to make some data structure to store the information. We will need to get the list of all entities first.
		
		//System.out.println("Physics Update: " + physicsUpdateCount++ );
		
		ArrayList<PhysicsNetworkBody> networkBodies = new ArrayList<PhysicsNetworkBody>();
		ArrayList<RigidBody> bodies =  Globals.getBodies();

		
		//System.out.println("Physics Bodies: " + bodies.size());
		
		for(RigidBody rb : bodies){
			PhysicsNetworkBody pnb = new PhysicsNetworkBody();
			pnb.hash = rb.bodyIdHash; //We use the hash to make sure we update the correct object on the client
			pnb.clientID = rb.clientID;
			pnb.angularVelocity = rb.getAngularVelocity(new Vector3f());
			pnb.linearVelocity = rb.getLinearVelocity(new Vector3f());
			pnb.orientation = rb.getOrientation(new Quat4f());
			pnb.worldTransform = rb.getWorldTransform(new Transform());			
			networkBodies.add(pnb);
		}	
			
		NetworkMessage outMessage = new NetworkMessage();
		outMessage.physicsUpdate = true;
		outMessage.physicsData = networkBodies;
		queueMessage(outMessage);
		
		
		//Once we have done all this we can process the waiting messages to be sent.
		for(NetworkMessage msg : sendQueue){
			sendMessage(msg);
		}
		
	}
				
	void queueMessage(NetworkMessage msg){
		sendQueue.add(msg);
	}
	
	void sendMessage(NetworkMessage msg) {
		try {
			out.writeObject(msg);
			out.flush();		
			out.reset();
			sendQueue.remove(msg);			
		} catch (IOException ioException) {
			ioException.printStackTrace();
			System.exit(0);
		}
	}
	
}
