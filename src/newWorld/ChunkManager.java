package newWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import newEntities.PhysicsEntity;
import newMain.Globals;
import newMain.IModule;
import newNetworking.ChunkData;

public class ChunkManager implements IModule {

	int chunkSize, blockSize;
	ArrayList<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;

	WorldRenderer renderer;

	@Override
	public void process() {
		Globals.setLoadedChunks(loadedChunks);
	}

	@Override
	public void setUp() {

		loadedChunks = new ArrayList<Chunk>();
		chunks = new HashMap<ChunkID, Chunk>();

		if (Globals.isServer()) { // We only initialise the chunks on the server
									// since the client downloads them when they
									// connect.

			for (int x = -10; x < 10; x++) {
				for (int y = -1; y < 0; y++) {
					for (int z = -10; z < 10; z++) {
						Chunk testChunk = new Chunk(x, y, z, 16, (float) 2);
						chunks.put(new ChunkID(x, y, z), testChunk);
						loadedChunks.add(testChunk);
					}
				}

			}

			Globals.setLoadedChunks(loadedChunks);

		}

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
