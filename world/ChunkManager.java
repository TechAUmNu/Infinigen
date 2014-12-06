package world;

//Class to manage loading and rendering of chunks
public class ChunkManager {
	private int viewDistance = 10;
	private int maxLoadedChunks = 100;
	
	public ChunkManager(){
		
	}
	
	public Chunk LoadChunk(int x, int y, int z){
		return new ChunkLoader().Load(x, y, z);
	}
	
	
}
