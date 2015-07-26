package main.java.com.ionsystems.infinigen.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3f;

import com.sudoplay.joise.module.Module;
import com.sudoplay.joise.module.ModuleAutoCorrect;
import com.sudoplay.joise.module.ModuleBias;
import com.sudoplay.joise.module.ModuleCache;
import com.sudoplay.joise.module.ModuleCombiner;
import com.sudoplay.joise.module.ModuleCombiner.CombinerType;
import com.sudoplay.joise.module.ModuleFractal;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;
import com.sudoplay.joise.module.ModuleFractal.FractalType;
import com.sudoplay.joise.module.ModuleGradient;
import com.sudoplay.joise.module.ModuleScaleDomain;
import com.sudoplay.joise.module.ModuleScaleOffset;
import com.sudoplay.joise.module.ModuleSelect;
import com.sudoplay.joise.module.ModuleTranslateDomain;
import com.sudoplay.joise.module.SourcedModule;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.networking.ChunkData;

public class ChunkManager implements IModule {

	int chunkSize = 32;
	float blockSize = 2f;
	ArrayList<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;
	Module terrainNoise;
	WorldRenderer renderer;

	@Override
	public void process() {
		Globals.setLoadedChunks(loadedChunks);
	}

	@Override
	public void setUp() {
		initNoiseGenerator();
		loadedChunks = new ArrayList<Chunk>();
		chunks = new HashMap<ChunkID, Chunk>();

		//if (Globals.isServer()) { // We only initialise the chunks on the server
									// since the client downloads them when they
									// connect.

			int xNum = 10;
			int yNum = 1;
			int zNum = 10;
			int count = xNum * 2 * yNum * zNum * 2;
			System.out.println("Number of chunks to generate: " + count );
			for (int x = -xNum; x < xNum; x++) {
				for (int y = -yNum; y < 0; y++) {
					for (int z = -zNum; z < zNum; z++) {
						Chunk testChunk = new Chunk(x, y, z, chunkSize, (float) blockSize,terrainNoise);
						chunks.put(new ChunkID(x, y, z), testChunk);
						loadedChunks.add(testChunk);
					}
				}

			}

			Globals.setLoadedChunks(loadedChunks);

		//}

	}
	
	private Vector3f findContainingChunk(int worldX, int worldY, int worldZ){
		//To find the chunk we just divide the coord by the chunk size * block size
		float multiplyer = chunkSize * blockSize;		
		Vector3f chunkLocation = new Vector3f();		
		chunkLocation.x =  (float) Math.floor(worldX / multiplyer);
		chunkLocation.y =  (float) Math.floor(worldY / multiplyer);
		chunkLocation.z =  (float) Math.floor(worldZ / multiplyer);		
		return chunkLocation;		
	}
	

	private void initNoiseGenerator(){
		Random random = new Random();
	    long seed = random.nextLong();

	    ModuleFractal gen = new ModuleFractal();
	    gen.setAllSourceBasisTypes(BasisType.SIMPLEX);
	    gen.setAllSourceInterpolationTypes(InterpolationType.CUBIC);
	    gen.setNumOctaves(2);
	    gen.setFrequency(1);
	    gen.setType(FractalType.RIDGEMULTI);
	    gen.setSeed(898456);
	    

	    /*
	     * ... route it through an autocorrection module...
	     * 
	     * This module will sample it's source multiple times and attempt to
	     * auto-correct the output to the range specified.
	     */
	    ModuleAutoCorrect ac = new ModuleAutoCorrect();
	    ac.setSource(gen); // set source (can usually be either another Module or a
	                       // double value; see specific module for details)
	    ac.setRange(0.0f, 32.0f); // set the range to auto-correct to
	    ac.setSamples(10000); // set how many samples to take
	    ac.calculate(); // perform the caclulations
	    
	    
	    terrainNoise = ac;
	}
	
	
	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
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
		// Chunk c = new Chunk(x, y, z, chunkSize, blockSize);
	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

}
