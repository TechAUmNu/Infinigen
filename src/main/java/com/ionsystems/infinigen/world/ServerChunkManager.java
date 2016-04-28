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
import main.java.com.ionsystems.infinigen.networking.ChunkData;

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
	ArrayList<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;
	Module terrainNoise;

	Vector3f chunkLocation = new Vector3f();
	public static int loadDistance = 25;
	long seed = 828382;
	int state = 0;
	ExecutorService pool = Executors.newFixedThreadPool(24); // creates a pool of threads for the Future to draw from

	public void process() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		ArrayList<ChunkID> toLoad = new ArrayList<ChunkID>();
		for (int x = -loadDistance; x < loadDistance + 1; x++) {
			for (int z = -loadDistance; z < loadDistance + 1; z++) {
				if (inCircle(0, 0, loadDistance, x, z)) {
					toLoad.add(new ChunkID(x,-1,z));
				}
			}
		}
		loadChunks(toLoad);

		Globals.setLoadedChunks(loadedChunks);

	}

	boolean inCircle(double centerX, double centerY, double radius, double x, double y) {
		double square_dist = Math.pow((centerX - x), 2) + Math.pow((centerY - y), 2);
		return square_dist <= Math.pow(radius, 2);

	}

	public void setUp() {

		initNoiseGenerator();
		loadedChunks = new ArrayList<Chunk>();
		chunks = new HashMap<ChunkID, Chunk>();

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
		ac.calculate(); // perform the caclulations

		terrainNoise = ac;
	}

	public void update() {
		for (Chunk c : loadedChunks) {
			c.update();
		}

		if (!Globals.isServer()) {
			if (!Globals.getChunkUpdate().isEmpty()) {
				for (ChunkData cd : Globals.getChunkUpdate()) {
					Chunk c = new Chunk(cd);
					chunks.put(new ChunkID(cd.x, cd.y, cd.z), c);
					loadedChunks.add(c);
				}
				Globals.setLoadedChunks(loadedChunks);
				Globals.getChunkUpdate().clear();
			}
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
		System.out.println("HER\n\n\n\n\n\n\n\n\n\n");
		for (Future<Chunk> f : loadingChunks){
			
			try {
				Chunk chunk = f.get();
				chunks.put(chunk.chunkID, chunk);
				loadedChunks.add(chunk);
				System.out.println("Chunks Loaded: " + loadedChunks.size());
			} catch (InterruptedException | ExecutionException e) {				
				e.printStackTrace();
			}
			
		}		
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		setUp();

		// while (Globals.isRunning()) {
		process();
		update();
		state++;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

	}

	

}
