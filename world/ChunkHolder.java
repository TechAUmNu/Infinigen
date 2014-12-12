package world;

import org.magicwerk.brownies.collections.GapList;


public class ChunkHolder {
	
	//The max number of chunks that can be loaded in each axis
	private int MAX_CHUNKS = 50;
	
	//3d array of chunks for fast access
	private Chunk[][][] chunks = new Chunk[MAX_CHUNKS][MAX_CHUNKS][MAX_CHUNKS];
	
	//Using GapList since it has extremely good random performance
	private GapList<ChunkID> loadedChunks = new GapList<ChunkID>();
	
	/**
	 * Checks if a chunk is loaded
	 * @param x X Chunk location
	 * @param y Y Chunk location
	 * @param z Z Chunk location
	 * @return If the chunk is loaded
	 */
	public Boolean chunkLoaded(int x, int y, int z){
		if(chunks[x][y][z] != null)	
			return true;
		return false;		
	}
	
	/**
	 * Gets a chunk
	 * @param x X Chunk location
	 * @param y Y Chunk location
	 * @param z Z Chunk location
	 * @return The chunk if loaded, otherwise null
	 */
	public Chunk GetChunk(int x, int y, int z){
		return chunks[x][y][z];
	}		
	
	/**
	 * Loads a chunk
	 * @param x X Chunk location
	 * @param y Y Chunk location
	 * @param z Z Chunk location
	 * @param worldLocation Location of the world folder
	 * @return The loaded chunk
	 */
	public Chunk LoadChunk(int x, int y, int z, String worldLocation){
		chunks[x][y][z] = new ChunkLoader(worldLocation).Load(x, y, z);
		loadedChunks.add(new ChunkID(x,y,z));
		//chunks[x][y][z].Save();
		return chunks[x][y][z];
	}

	/**
	 * Unloads a specific chunk
	 * @param x X chunk location
	 * @param y Y chunk location
	 * @param z Z chunk location
	 */
	public void UnloadChunk(int x, int y, int z){
		new ChunkLoader("").Unload(chunks[x][y][z]);
		loadedChunks.remove(chunks[x][y][z]);
		chunks[x][y][z].CleanUp();
		chunks[x][y][z] = null;
	}
	
	/**
	 * Unloads all loaded chunks
	 */
	public void UnloadChunks(){
		ChunkLoader cl = new ChunkLoader("");
		for(ChunkID c : loadedChunks){
			cl.Unload(chunks[c.x][c.y][c.z]);
			chunks[c.x][c.y][c.z] = null;
		}	
		loadedChunks.clear();
	}

}
