package world;

public class ChunkHolder {
	private Chunk[][][] chunks;	
	
	public Boolean chunkLoaded(int x, int y, int z){
		if(chunks[x][y][z] != null)	
			return true;
		return false;		
	}
	
	public Chunk getChunk(int x, int y, int z){
		return chunks[x][y][z];
	}
	
	public void setChunk(int x, int y, int z, Chunk c){
		chunks[x][y][z] = c;
	}
	
	public Chunk loadChunk(int x, int y, int z, String worldLocation){
		return chunks[x][y][z] = new ChunkLoader(worldLocation).Load(x, y, z);
	}
}
