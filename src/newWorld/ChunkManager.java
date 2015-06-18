package newWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import newEntities.PhysicsEntity;
import newMain.IModule;

public class ChunkManager implements IModule {

	int chunkSize, blockSize;
	ArrayList<Chunk> loadedChunks;
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
		renderer.render();
	}

	@Override
	public void update() {
		for(Chunk c : loadedChunks){
			c.update();
		}
	}
	
	
	public void loadChunk(int x, int y, int z){
		Chunk c = new Chunk(x, y, z, chunkSize, blockSize);
	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}

}
