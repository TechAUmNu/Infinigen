package newWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import newEntities.PhysicsEntity;
import newMain.Globals;
import newMain.IModule;

public class ChunkManager implements IModule {

	int chunkSize, blockSize;
	ArrayList<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;

	WorldRenderer renderer;

	@Override
	public void process() {
		Globals.setVisibleChunks(loadedChunks);

	}

	@Override
	public void setUp() {

		long startTime = System.currentTimeMillis();
		loadedChunks = new ArrayList<Chunk>();
		chunks = new HashMap<ChunkID, Chunk>();

		for (int x = -10; x < 10; x++) {
			for (int y = -1; y < 0; y++) {
				for (int z = -10; z < 10; z++) {

					Chunk testChunk = new Chunk(x, y, z, 16, (float) 2);
					chunks.put(new ChunkID(x, y, z), testChunk);
					loadedChunks.add(testChunk);
				}
			}

		}

		long endTime = System.currentTimeMillis();
		long total = endTime - startTime;
		System.out.println("Total Time: " + total);
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
