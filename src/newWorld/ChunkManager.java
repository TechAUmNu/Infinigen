package newWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import newMain.IModule;

public class ChunkManager implements IModule {

	int chunkSize, blockSize;
	List<Chunk> loadedChunks;
	HashMap<ChunkID, Chunk> chunks;

	WorldRenderer renderer;

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUp() {
		loadedChunks = new ArrayList<Chunk>();
		chunks = new HashMap<ChunkID, Chunk>();
		renderer = new WorldRenderer();

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
