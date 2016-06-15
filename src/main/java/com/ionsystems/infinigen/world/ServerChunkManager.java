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
import com.sudoplay.joise.module.ModuleCache;
import com.sudoplay.joise.module.ModuleGradient;
import com.sudoplay.joise.module.ModuleInvert;
import com.sudoplay.joise.module.ModuleScaleDomain;
import com.sudoplay.joise.module.ModuleScaleOffset;
import com.sudoplay.joise.module.ModuleSelect;
import com.sudoplay.joise.module.ModuleTranslateDomain;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleFractal.FractalType;

//This handles all loading and unloading of chunks. The chunks within the set range of each camera must be kept loaded so switching camera is fast.

// Current network usage per chunk: 280KB

public class ServerChunkManager implements Runnable {

	public static int chunkSize = 16;
	public static int chunkHeight = 512;
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
		// ground_gradient
	    ModuleGradient groundGradient = new ModuleGradient();
	    groundGradient.setGradient(0, 0, 0, 1);

	    /*
	     * lowland
	     */

	    // lowland_shape_fractal
	    ModuleFractal lowlandShapeFractal = new ModuleFractal(FractalType.BILLOW, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    lowlandShapeFractal.setNumOctaves(2);
	    lowlandShapeFractal.setFrequency(0.25);
	    lowlandShapeFractal.setSeed(seed);

	    // lowland_autocorrect
	    ModuleAutoCorrect lowlandAutoCorrect = new ModuleAutoCorrect(0, 128);
	    lowlandAutoCorrect.setSource(lowlandShapeFractal);
	    lowlandAutoCorrect.calculate();

	    // lowland_scale
	    ModuleScaleOffset lowlandScale = new ModuleScaleOffset();
	    lowlandScale.setScale(0.25);
	    lowlandScale.setOffset(384);
	    lowlandScale.setSource(lowlandAutoCorrect);

	    // lowland_y_scale
	    ModuleScaleDomain lowlandYScale = new ModuleScaleDomain();
	    lowlandYScale.setScaleY(0);
	    lowlandYScale.setSource(lowlandScale);

	    // lowland_terrain
	    ModuleTranslateDomain lowlandTerrain = new ModuleTranslateDomain();
	    lowlandTerrain.setAxisYSource(lowlandYScale);
	    lowlandTerrain.setSource(groundGradient);
	    
	    
	    /*
	     * highland
	     */

	    // highland_shape_fractal
	    ModuleFractal highlandShapeFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    highlandShapeFractal.setNumOctaves(4);
	    highlandShapeFractal.setFrequency(1);
	    highlandShapeFractal.setSeed(seed);

	    // highland_autocorrect
	    ModuleAutoCorrect highlandAutoCorrect = new ModuleAutoCorrect(-128, 128);
	    highlandAutoCorrect.setSource(highlandShapeFractal);
	    highlandAutoCorrect.calculate();

	    // highland_scale
	    ModuleScaleOffset highlandScale = new ModuleScaleOffset();
	    highlandScale.setScale(0.25);
	    highlandScale.setOffset(384);
	    highlandScale.setSource(highlandAutoCorrect);

	    // highland_y_scale
	    ModuleScaleDomain highlandYScale = new ModuleScaleDomain();
	    highlandYScale.setScaleY(0);
	    highlandYScale.setSource(highlandScale);

	    // highland_terrain
	    ModuleTranslateDomain highlandTerrain = new ModuleTranslateDomain();
	    highlandTerrain.setAxisYSource(highlandYScale);
	    highlandTerrain.setSource(groundGradient);

	    
	    /*
	     * mountain
	     */

	    // mountain_shape_fractal
	    ModuleFractal mountainShapeFractal = new ModuleFractal(FractalType.RIDGEMULTI, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    mountainShapeFractal.setNumOctaves(8);
	    mountainShapeFractal.setFrequency(0.5);
	    mountainShapeFractal.setSeed(seed);

	    // mountain_autocorrect
	    ModuleAutoCorrect mountainAutoCorrect = new ModuleAutoCorrect(128, 384);
	    mountainAutoCorrect.setSource(mountainShapeFractal);
	    mountainAutoCorrect.calculate();

	    // mountain_scale
	    ModuleScaleOffset mountainScale = new ModuleScaleOffset();
	    mountainScale.setScale(1);
	    mountainScale.setOffset(-128);
	    mountainScale.setSource(mountainAutoCorrect);

	    // mountain_y_scale
	    ModuleScaleDomain mountainYScale = new ModuleScaleDomain();
	    mountainYScale.setScaleY(0.1);
	    mountainYScale.setSource(mountainScale);

	    // mountain_terrain
	    ModuleTranslateDomain mountainTerrain = new ModuleTranslateDomain();
	    mountainTerrain.setAxisYSource(mountainYScale);
	    mountainTerrain.setSource(groundGradient);
	    
	    /*
	     * terrain
	     */

	    // terrain_type_fractal
	    ModuleFractal terrainTypeFractal = new ModuleFractal(FractalType.FBM, BasisType.GRADIENT, InterpolationType.QUINTIC);
	    terrainTypeFractal.setNumOctaves(6);
	    terrainTypeFractal.setFrequency(0.2);
	    terrainTypeFractal.setSeed(seed);

	    // terrain_autocorrect
	    ModuleAutoCorrect terrainAutoCorrect = new ModuleAutoCorrect(0, 1);
	    terrainAutoCorrect.setSource(terrainTypeFractal);
	    terrainAutoCorrect.calculate();

	    // terrain_type_y_scale
	    ModuleScaleDomain terrainTypeYScale = new ModuleScaleDomain();
	    terrainTypeYScale.setScaleY(0);
	    terrainTypeYScale.setSource(terrainAutoCorrect);

	    // terrain_type_cache
	    ModuleCache terrainTypeCache = new ModuleCache();
	    terrainTypeCache.setSource(terrainTypeYScale);

	    // highland_mountain_select
	    ModuleSelect highlandMountainSelect = new ModuleSelect();
	    highlandMountainSelect.setLowSource(highlandTerrain);
	    highlandMountainSelect.setHighSource(mountainTerrain);
	    highlandMountainSelect.setControlSource(terrainTypeCache);
	    highlandMountainSelect.setThreshold(0.95);
	    highlandMountainSelect.setFalloff(0.5);

	    // highland_lowland_select
	    ModuleSelect highlandLowlandSelect = new ModuleSelect();
	    highlandLowlandSelect.setLowSource(lowlandTerrain);
	    highlandLowlandSelect.setHighSource(highlandMountainSelect);
	    highlandLowlandSelect.setControlSource(terrainTypeCache);
	    highlandLowlandSelect.setThreshold(0.6);
	    highlandLowlandSelect.setFalloff(0.5);

	    // highland_lowland_select_cache
	    ModuleCache highlandLowlandSelectCache = new ModuleCache();
	    highlandLowlandSelectCache.setSource(highlandLowlandSelect);

	    
	    /*
	     * ... route it through an autocorrection module...
	     * 
	     * This module will sample it's source multiple times and attempt to
	     * auto-correct the output to the range specified.
	     */
	   
//		ModuleAutoCorrect ac = new ModuleAutoCorrect();
//		ac.setSource(highlandLowlandSelectCache); // set source (can usually be either another Module
//							// or a
//							// double value; see specific module for details)
//		ac.setRange(0.0f, chunkSize); // set the range to auto-correct to
//		ac.setSamples(10000); // set how many samples to take
//		ac.calculate(); // perform the calculations

		
		terrainNoise = highlandLowlandSelectCache;
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
				return new Chunk(chunkID, chunkID.x, chunkID.y, chunkID.z, chunkSize, chunkHeight, blockSize, terrainNoise);
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
			ArrayList<NetworkChunkData> ncdList = new ArrayList<NetworkChunkData>();
			for(ChunkID cid : pendingChunks.get(c)){
				ncdList.add(chunks.get(cid).getNcd());			
			}		
			
			pendingChunks.get(c).clear();;
			NetworkMessage msg = new NetworkMessage();
			msg.ncd = ncdList;			
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
