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
import main.java.com.ionsystems.infinigen.networking.ChunkData;

//This handles all loading and unloading of chunks. The chunks within the set range of each camera must be kept loaded so switching camera is fast.

public class ChunkManager implements Runnable {

	public static int chunkSize = 64;
	float blockSize = 1f;
	CopyOnWriteArrayList<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;
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

		// We dont want to let this thread use 100% cpu.

		cameraChunkLocation = findContainingChunk(Globals.getCameraPosition());
		// Where the camera is in chunk space
		// System.out.println(cameraChunkLocation);

		cameraX = (int) cameraChunkLocation.x;
		cameraZ = (int) cameraChunkLocation.z;

		ArrayList<ChunkID> toUnload = new ArrayList<ChunkID>();
		for (ChunkID c : chunks.keySet()) {
			if (!inCircle(cameraX, cameraZ, loadDistance, c.x, c.z)) {
				toUnload.add(new ChunkID(c.x, c.y, c.z));
			}
		}
		for (ChunkID c : toUnload) {
			chunks.get(c).cleanUp();
			loadedChunks.remove(chunks.get(c));
			Globals.getLoadedChunks().remove(chunks.get(c));
			chunks.remove(c);
		}
		toUnload = null;

		for (int x = -loadDistance + cameraX; x < loadDistance + cameraX + 1; x++) {
			for (int z = -loadDistance + cameraZ; z < loadDistance + cameraZ + 1; z++) {
				if (inCircle(cameraX, cameraZ, loadDistance, x, z)) {
					loadChunk(x, -1, z);

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

		initNoiseGenerator();
		loadedChunks = new CopyOnWriteArrayList<Chunk>();
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
		 * This module will sample it's source multiple times and attempt to
		 * auto-correct the output to the range specified.
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

	public void loadChunk(int x, int y, int z) {

		if (!chunks.containsKey(new ChunkID(x, y, z))) {
			Chunk testChunk = new Chunk(x, y, z, chunkSize, blockSize, terrainNoise);
			chunks.put(new ChunkID(x, y, z), testChunk);
			loadedChunks.add(testChunk);
		}
	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		setUp();

		//while (Globals.isRunning()) {
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
