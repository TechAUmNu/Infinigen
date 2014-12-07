package world;

import org.magicwerk.brownies.collections.GapList;


public class ChunkHolder {
	private int MAX_CHUNKS = 20;
	private Chunk[][][] chunks = new Chunk[MAX_CHUNKS][MAX_CHUNKS][MAX_CHUNKS];
	private GapList<ChunkID> loadedChunks = new GapList<ChunkID>();
	public Boolean chunkLoaded(int x, int y, int z){
		if(chunks[x][y][z] != null)	
			return true;
		return false;		
	}
	
	public Chunk GetChunk(int x, int y, int z){
		return chunks[x][y][z];
	}		
	
	public Chunk LoadChunk(int x, int y, int z, String worldLocation){
		chunks[x][y][z] = new ChunkLoader(worldLocation).Load(x, y, z);
		loadedChunks.add(new ChunkID(x,y,z));
		return chunks[x][y][z];
	}
	
	public void Render(){
		for(ChunkID c : loadedChunks){
			chunks[c.x][c.y][c.z].Render();
		}
		
	}
	
	public void UnloadChunk(int x, int y, int z){
		new ChunkLoader("").Unload(chunks[x][y][z]);
		loadedChunks.remove(chunks[x][y][z]);
		chunks[x][y][z] = null;
	}
	
	
	public static void main(String[] args){
		ChunkHolder c = new ChunkHolder();
		
		System.out.println(c.chunkLoaded(0, 0, 0));
		c.LoadChunk(0, 0, 0, "C:\\Testing\\ChunkTesting");
		System.out.println(c.chunkLoaded(0, 0, 0));
		c.UnloadChunk(0, 0, 0);
		System.out.println(c.chunkLoaded(0, 0, 0));
		
	}
}
