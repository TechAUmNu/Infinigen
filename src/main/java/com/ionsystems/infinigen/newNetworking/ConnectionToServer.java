package main.java.com.ionsystems.infinigen.newNetworking;

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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.Timer;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;

import main.java.com.ionsystems.infinigen.newEntities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.newMain.Globals;

//For new objects and events we need to tell the server, once the object has been created and added on the server the client receives updates on its position to make sure everything is in sync across clients

public class ConnectionToServer implements Runnable, ActionListener {

	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	GZIPOutputStream gzipOut;
	Client client;
	NetworkMessage inMessage;
	int chunkCount = 0, currentChunk = 0;;
	ArrayList<ChunkData> chunkUpdate;
	Timer timer;
	CopyOnWriteArrayList<NetworkMessage> sendQueue = new CopyOnWriteArrayList<NetworkMessage>();

	ConnectionToServer(Client c) {
		client = c;

	}

	public void run() {
		try {
			// 1. creating a socket to connect to the server

			System.out.println("about to connect to server at: " + Globals.getIp() + ":" + Globals.getPort());
			socket = new Socket(Globals.getIp(), Globals.getPort());
			socket.setPerformancePreferences(0, 1, 2);
			socket.setTcpNoDelay(true);
			System.out.println("Connected to localhost in port " + +Globals.getPort());
			// 2. get Input and Output streams
			System.out.println("Creating output stream");
			gzipOut = new GZIPOutputStream(socket.getOutputStream(), 4096, true);
			out = new ObjectOutputStream(new BufferedOutputStream(gzipOut));
			System.out.println("Flushing output");
			out.flush();

			System.out.println("Creating input stream");
			in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(socket.getInputStream())));

			// 3: Communicating with the server
			System.out.println("Connected to server, sending client info");
			// Send our client info to the server.
			NetworkMessage outMessage = new NetworkMessage();
			outMessage.client = client;
			queueMessage(outMessage);

			// Now we want to know what the current terrain is, so we get a
			// chunk update which will get all currently loaded chunks on the
			// server (Might be an idea in future to make it only around a
			// specific area?)

			outMessage = new NetworkMessage();
			outMessage.chunkUpdate = true;
			queueMessage(outMessage);

			timer = new Timer(100, this);
			timer.start(); // Timer for regular updates to client (100ms)

			do {
				try {
					// System.out.println("Waiting on message from server");
					inMessage = (NetworkMessage) in.readObject();
					// System.out.println("Message received from server");
					if (!inMessage.disconnect) {
						processMessage();
					}

				} catch (ClassNotFoundException classNot) {
					System.err.println("data received in unknown format");
				} catch (EOFException e) {
					// Ignore this error since it will always occur when you
					// quit.
				}
			} while (!inMessage.disconnect);
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
		if (inMessage.chunkUpdate) {
			chunkUpdate();
		}
		if (inMessage.physicsUpdate) {
			physicsUpdate();
		}
		if(inMessage.newEntity){
			newEntity();
		}
	}

	private void newEntity() {
		for(PhysicsEntity e : inMessage.entityData){
			Globals.getPhysics().getProcessor().addPhysicsEntity(e); //Thats easy :D
			// now we just at the entities to the list we already have			
			Globals.addEntity(e, true);
		}		
	}

	private void physicsUpdate() {
		// We are now updating the physics objects locations

		// We will receive a list of PhysicsNetworkBody which has the info about
		// where stuff is and how its moving

		// So we need to look through our list of rigid bodies and update the
		// matching ones

		ArrayList<PhysicsNetworkBody> networkBodies = inMessage.physicsData;
		ArrayList<RigidBody> bodies = Globals.getPhysics().getProcessor().getBodies();

		System.out.println("Physics update: " + networkBodies.size());

		for (PhysicsNetworkBody body : networkBodies) {
			for (RigidBody rb : bodies) {
				if (body.hash == rb.hashCode()) {
					System.out.println("MATCH");
					rb.setAngularVelocity(body.angularVelocity);
					rb.setLinearVelocity(body.linearVelocity);
					rb.setWorldTransform(body.worldTransform);
					break;
				}

			}
		}

	}

	private void chunkUpdate() {

		// We are now updating our chunks

		// First we will receive the number of chunks that are being loaded
		if (inMessage.chunkCount > 0) {
			chunkCount = inMessage.chunkCount;
			chunkUpdate = new ArrayList<ChunkData>();
			currentChunk = 0;
		}

		// Then we will receive the chunks

		if (inMessage.chunkData != null) {
			chunkUpdate.add(inMessage.chunkData);
			System.out.println("Chunk: " + currentChunk + "/" + chunkCount);
			currentChunk++;

		}

		if (inMessage.chunkUpdateComplete) {
			System.out.println("Recieved: " + currentChunk + "/" + chunkCount + " Chunks in chunk update");
			Globals.setChunkUpdate(chunkUpdate);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// So the main thing for the client is when new objects are created, we
		// need to make sure the server knows about them,
		// The server actually only needs to know about the rigid body, but the other clients need to know what it is we are drawing so we must send the whole entity
		
		
		//We get the list of new entities from globals.
		
		ArrayList<PhysicsEntity> newEntities = Globals.getNewEntities();		
		//System.out.println("SENDING NEW ENTITES: " + newEntities.size());
		NetworkMessage outMessage = new NetworkMessage();
		outMessage.newEntity = true; // We set this so the client knows it is part of the chunk update			
		outMessage.entityData = newEntities; //TODO: Clearly this is a stupid idea so need to change this to use named models.	
		queueMessage(outMessage); //This will probably be huge *Oh dear :S*
		
		
		

		// Once we have done all this we can process the waiting messages to be
		// sent.
		for (NetworkMessage msg : sendQueue) {
			sendMessage(msg);
		}
	}

	void queueMessage(NetworkMessage msg) {
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
		}
	}

}
