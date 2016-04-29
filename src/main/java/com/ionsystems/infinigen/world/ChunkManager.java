package main.java.com.ionsystems.infinigen.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.messages.Messaging;
import main.java.com.ionsystems.infinigen.messages.Tag;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.newNetworking.NetworkMessage;

//This handles all loading and unloading of chunks. The chunks within the set range of each camera must be kept loaded so switching camera is fast.

public class ChunkManager implements Runnable {

	public static int chunkSize = 64;
	float blockSize = 1f;
	CopyOnWriteArrayList<NetworkChunkRenderingData> loadedChunks;
	HashMap<ChunkID, NetworkChunkRenderingData> chunks;
	ArrayList<ChunkID> pendingChunks = new ArrayList<ChunkID>();
	Module terrainNoise;
	WorldRenderer renderer;
	Vector3f chunkLocation = new Vector3f();
	public static int loadDistance = 5;
	long seed = 828382;
	int state = 0;
	int cameraX, cameraZ;
	Vector3f cameraChunkLocation;

	public void process() {

		// Here we will decide which chunks to load/unload

		// First we will do loading

		// loadDistance is how far away in a circle to load chunks
		// So first we shall just loop through every chunk in the area and load
		// the chunk if it isn't already.
		while (Globals.loading() && Globals.getCameraPosition() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// We don't want to let this thread use 100% CPU.

		cameraChunkLocation = findContainingChunk(Globals.getCameraPosition());
		// Where the camera is in chunk space
		// System.out.println(cameraChunkLocation);

		cameraX = (int) cameraChunkLocation.x;
		cameraZ = (int) cameraChunkLocation.z;

//		ArrayList<ChunkID> toUnload = new ArrayList<ChunkID>();
//		for (ChunkID c : chunks.keySet()) {
//			if (!inCircle(cameraX, cameraZ, loadDistance, c.x, c.z)) {
//				toUnload.add(new ChunkID(c.x, c.y, c.z));
//			}
//		}
//		for (ChunkID c : toUnload) {			
//			loadedChunks.remove(chunks.get(c));
//			Globals.getLoadedChunks().remove(chunks.get(c));
//			chunks.remove(c);
//		}
//		toUnload = null;

		ArrayList<ChunkID> toLoad = new ArrayList<ChunkID>();
		for (int x = -loadDistance + cameraX; x < loadDistance + cameraX + 1; x++) {
			for (int z = -loadDistance + cameraZ; z < loadDistance + cameraZ + 1; z++) {
				if (inCircle(cameraX, cameraZ, loadDistance, x, z)) {
					if(!chunks.containsKey(new ChunkID(x,-1,z)) &&  !pendingChunks.contains(new ChunkID(x,-1,z))){
						toLoad.add(new ChunkID(x,-1,z));
						pendingChunks.add(new ChunkID(x,-1,z));
					}
				}
			}
		}
		if(!toLoad.isEmpty()){
			// Send the chunk request off to the networking system
			NetworkMessage msg = new NetworkMessage();
			ArrayList<ChunkID> toBeLoaded = new ArrayList<ChunkID>();
			toBeLoaded.addAll(toLoad);
			msg.chunkRequest = toBeLoaded;
			msg.client = Globals.getClient();	
			msg.tag = Tag.NetworkChunkRequest;
			Messaging.addMessage(Tag.NetworkLatencySend, msg);
			toLoad.clear();
		}
		
		while(Messaging.anyMessages(Tag.NetworkChunkUpdate)){
			// The new chunk data comes in a form that can be rendered immediately with no further processing. So we can just add it directly to the loading queue.
			NetworkMessage incomingMsg = (NetworkMessage) Messaging.takeLatestMessage(Tag.NetworkChunkUpdate);
			System.out.println("Processing " + incomingMsg.ncrd.size() + " Chunks : " + Tag.NetworkChunkUpdate);
			for(NetworkChunkRenderingData ncrd : incomingMsg.ncrd){
				
				if(!chunks.containsKey(ncrd.chunkID)){
				
					Globals.getLoadingLock().writeLock().lock();
					Globals.getLoader().addChunkToLoadQueue(ncrd);
					Globals.getLoadingLock().writeLock().unlock();
					
					chunks.put(ncrd.chunkID, ncrd);
					loadedChunks.add(ncrd);
					pendingChunks.remove(ncrd.chunkID);
				}
			}
		}
		

		Globals.setLoadedChunks(loadedChunks);

	}

	boolean inCircle(double centerX, double centerY, double radius, double x, double y) {
		double square_dist = Math.pow((centerX - x), 2) + Math.pow((centerY - y), 2);
		return square_dist <= Math.pow(radius, 2);

	}

	public void setUp() {
		loadedChunks = new CopyOnWriteArrayList<NetworkChunkRenderingData>();
		chunks = new HashMap<ChunkID, NetworkChunkRenderingData>();
		Globals.setLoadedChunks(loadedChunks);
	}

	private Vector3f findContainingChunk(Vector3f location) {
		// To find the chunk we just divide the coord by the chunk size * block
		// size
		float multiplyer = chunkSize * blockSize;

		chunkLocation.x = (float) Math.floor(location.x / multiplyer);
		chunkLocation.y = (float) Math.floor(location.y / multiplyer);
		chunkLocation.z = (float) Math.floor(location.z / multiplyer);
		return chunkLocation;
	}

	

	public void update() {
		// Here we will check for messages tagged with Tag.NetworkChunkUpdate
		
		
		
		
	}
	

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		setUp();

		while (Globals.isRunning()) {
			process();
			update();
			state++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
