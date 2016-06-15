package main.java.com.ionsystems.infinigen.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.lwjgl.util.vector.Vector3f;
import org.omg.CORBA.Object;

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
import main.java.com.ionsystems.infinigen.newNetworking.Client;
import main.java.com.ionsystems.infinigen.newNetworking.NetworkMessage;

//This handles all loading and unloading of chunks. The chunks within the set range of each camera must be kept loaded so switching camera is fast.

public class ChunkManager implements Runnable {

	public static int chunkSize = 16;
	public static int chunkHeight = 512;
	float blockSize = 1f;
	CopyOnWriteArrayList<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;
	ArrayList<ChunkID> pendingChunks = new ArrayList<ChunkID>();
	Module terrainNoise;
	WorldRenderer renderer;
	Vector3f chunkLocation = new Vector3f();
	public static int loadDistance = 2;
	long seed = 828382;
	int state = 0;
	int cameraX, cameraY, cameraZ;
	Vector3f cameraChunkLocation;
	ExecutorService pool = Executors.newFixedThreadPool(12); // creates a pool of threads for the Future to draw from

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
		cameraY = (int) cameraChunkLocation.y;


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
						for(int y = -1; y < 0+1; y++){
						if(!chunks.containsKey(new ChunkID(x,y,z)) &&  !pendingChunks.contains(new ChunkID(x,y,z))){
							toLoad.add(new ChunkID(x,y,z));
							pendingChunks.add(new ChunkID(x,y,z));
						}
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
		ArrayList<NetworkChunkData> chunksToLoad = new ArrayList<NetworkChunkData>();
		while(Messaging.anyMessages(Tag.NetworkChunkUpdate)){
			// The new chunk data comes in a form that can be rendered immediately with no further processing. So we can just add it directly to the loading queue.
			NetworkMessage incomingMsg = (NetworkMessage) Messaging.takeLatestMessage(Tag.NetworkChunkUpdate);
			System.out.println("Processing " + incomingMsg.ncd.size() + " Chunks : " + Tag.NetworkChunkUpdate);
			for(NetworkChunkData ncd : incomingMsg.ncd){
				
				if(!chunks.containsKey(ncd.chunkID)){				
					chunksToLoad.add(ncd);
				}
			}
		}
		
				
		loadChunks(chunksToLoad);
		Globals.setLoadedChunks(loadedChunks);
		

	}
	
	
	
	public void loadChunks(ArrayList<NetworkChunkData> chunksToLoad) {
		ArrayList<Future<Chunk>> loadingChunks = new ArrayList<Future<Chunk>>();
		for (NetworkChunkData ncd : chunksToLoad) {				
				loadingChunks.add(loadChunk(ncd));
		}		
		for (Future<Chunk> f : loadingChunks) {

			try {
				Chunk chunk = f.get();
				chunks.put(chunk.chunkID, chunk);
				loadedChunks.add(chunk);	
				pendingChunks.remove(chunk.chunkID);
				System.out.println("Chunks Loaded: " + loadedChunks.size());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		}		
	}

	public Future<Chunk> loadChunk(NetworkChunkData ncd) {
		return pool.submit(new Callable<Chunk>() {
			@Override
			public Chunk call() {
				return new Chunk(ncd);
			}
		});
	}
	
	
	boolean inCircle(double centerX, double centerZ, double radius, double x, double z) {
		double square_dist = Math.pow((centerX - x), 2)  + Math.pow((centerZ - z), 2);
		return square_dist <= Math.pow(radius, 2);
	}

	public void setUp() {
		loadedChunks = new CopyOnWriteArrayList<Chunk>();
		chunks = new HashMap<ChunkID, Chunk>();
		Globals.setLoadedChunks(loadedChunks);
	}

	private Vector3f findContainingChunk(Vector3f location) {
		// To find the chunk we just divide the coord by the chunk size * block
		// size
		

		chunkLocation.x = (float) Math.floor(location.x / chunkSize * blockSize);
		chunkLocation.y = (float) Math.floor(location.y / chunkHeight * blockSize);
		chunkLocation.z = (float) Math.floor(location.z / chunkSize * blockSize);
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
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
