package world;

import java.util.ArrayList;

//Class to manage loading and rendering of chunks
public class ChunkManager {
	private int viewDistance = 10;
	private int maxLoadedChunks = 100;
	private String worldLocation = "world";

	// 3d arraylist to store chunks
	private ChunkHolder chunks;

	public ChunkManager() {

	}

	public Chunk getChunk(int x, int y, int z) {
		if (chunks.chunkLoaded(x, y, z)) {
			return chunks.getChunk(x, y, z);
		} else {
			return null;
		}
	}

	public void GenerateChunk(int x, int y, int z) {
		if (!chunks.chunkLoaded(x, y, z)) {
			chunks.loadChunk(x, y, z, worldLocation);
		}
	}

}
