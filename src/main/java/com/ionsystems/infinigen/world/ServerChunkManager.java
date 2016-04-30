package main.java.com.ionsystems.infinigen.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.messages.Messaging;
import main.java.com.ionsystems.infinigen.messages.Tag;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.newNetworking.Client;
import main.java.com.ionsystems.infinigen.newNetworking.NetworkMessage;

import org.lwjgl.util.vector.Vector3f;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

//This handles all loading and unloading of chunks. The chunks within the set range of each camera must be kept loaded so switching camera is fast.

public class ServerChunkManager implements Runnable {

	public static int chunkSize = 64;
	float blockSize = 1f;
	CopyOnWriteArrayList<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;
	Module terrainNoise;

	Vector3f chunkLocation = new Vector3f();
	public static int loadDistance = 10;
	long seed = 828382;
	int state = 0;
	HashMap<Client, ArrayList<ChunkID>> pendingChunks = new HashMap<Client, ArrayList<ChunkID>>();
	ArrayList<ChunkID> chunksToBeLoaded = new ArrayList<ChunkID>();

	ExecutorService pool = Executors.newFixedThreadPool(12); // creates a pool of threads for the Future to draw from

	public void process() {
		
		// Here we will decide which chunks to load/unload

		// First we will do loading

		// loadDistance is how far away in a circle to load chunks
		// So first we shall just loop through every chunk in the area and load
		// the chunk if it isn't already.
//		while (Globals.loading() && Globals.getCameraPosition() == null) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

		// We don't want to let this thread use 100% CPU.

		// We want to load chunks as clients request them. No point in loading things we don't need.

		//The client is added to the interested list here
		ArrayList<ChunkID> toLoad = new ArrayList<ChunkID>();
		while (Messaging.anyMessages(Tag.NetworkChunkRequest)) {
			// The new chunk data comes in a form that can be rendered immediately with no further processing. So we can just add it directly to the loading
			// queue.
			NetworkMessage msg = (NetworkMessage) Messaging.takeLatestMessage(Tag.NetworkChunkRequest);
			System.out.println("Loading " + msg.chunkRequest.size() + " chunks for " + msg.client.username);
			for(ChunkID cid : msg.chunkRequest){
				if(!chunksToBeLoaded.contains(cid)){
					toLoad.add(cid);
				}
			}			
			pendingChunks.put(msg.client, msg.chunkRequest);
		}
		loadChunks(toLoad);

		Globals.setServerLoadedChunks(loadedChunks);

	}

	boolean inCircle(double centerX, double centerY, double radius, double x, double y) {
		double square_dist = Math.pow((centerX - x), 2) + Math.pow((centerY - y), 2);
		return square_dist <= Math.pow(radius, 2);

	}

	public void setUp() {

		initNoiseGenerator();
		loadedChunks = new CopyOnWriteArrayList<Chunk>();
		chunks = new HashMap<ChunkID, Chunk>();

		Globals.setServerLoadedChunks(loadedChunks);

	}

	private void initNoiseGenerator() {
		ModuleFractal gen = new ModuleFractal();
		gen.setAllSourceBasisTypes(BasisType.SIMPLEX);
		gen.setAllSourceInterpolationTypes(InterpolationType.CUBIC);
		gen.setNumOctaves(2);
		gen.setFrequency(1);
		gen.setType(FractalType.RIDGEMULTI);
		gen.setSeed(seed);

		/*
		 * ... route it through an autocorrection module...
		 * 
		 * This module will sample it's source multiple times and attempt to auto-correct the output to the range specified.
		 */
		ModuleAutoCorrect ac = new ModuleAutoCorrect();
		ac.setSource(gen); // set source (can usually be either another Module
							// or a
							// double value; see specific module for details)
		ac.setRange(0.0f, chunkSize); // set the range to auto-correct to
		ac.setSamples(10000); // set how many samples to take
		ac.calculate(); // perform the calculations

		terrainNoise = ac;
	}

	public void update() {
		for (Chunk c : loadedChunks) {
			c.update();
		}
	}

	public Future<Chunk> loadChunk(ChunkID chunkID) {
		return pool.submit(new Callable<Chunk>() {
			@Override
			public Chunk call() {
				return new Chunk(chunkID, chunkID.x, chunkID.y, chunkID.z, chunkSize, blockSize, terrainNoise);
			}
		});
	}

	public void loadChunks(ArrayList<ChunkID> chunksToLoad) {
		ArrayList<Future<Chunk>> loadingChunks = new ArrayList<Future<Chunk>>();
		for (ChunkID cid : chunksToLoad) {
			if (!chunks.containsKey(cid)) {
				loadingChunks.add(loadChunk(cid));
			}
		}
		
		for (Future<Chunk> f : loadingChunks) {

			try {
				Chunk chunk = f.get();
				chunks.put(chunk.chunkID, chunk);
				loadedChunks.add(chunk);				
				System.out.println("Chunks Loaded: " + loadedChunks.size());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

		}
		
		for(Client c : pendingChunks.keySet()){
			ArrayList<NetworkChunkRenderingData> ncrdList = new ArrayList<NetworkChunkRenderingData>();
			for(ChunkID cid : pendingChunks.get(c)){
				ncrdList.add(chunks.get(cid).getNcrd());
				
			}		
			
			pendingChunks.get(c).clear();;
			NetworkMessage msg = new NetworkMessage();
			msg.ncrd = ncrdList;			
			msg.tag = Tag.NetworkChunkUpdate;
			Messaging.addMessage(c.username + Tag.NetworkBandwidthSend.toString(), msg);	
			
		}
		pendingChunks.clear(); // This stops the server spamming updates back to the client
		chunksToBeLoaded.clear(); // This stops the server loading chunks multiple times		
		
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
