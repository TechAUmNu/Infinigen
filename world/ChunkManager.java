package world;

/**
 * Class to manage loading and rendering of chunks
 * @author Euan
 *
 */
public class ChunkManager {
	
	/**
	 * The current view distance
	 */
	private int viewDistance = 10;
	
	/**
	 * The max number of loaded chunks allowed
	 */
	private int maxLoadedChunks = 1000;
	
	/**
	 * The location of the world on disk
	 */
	private String worldLocation = "C:\\Testing\\ChunkTesting";
	
	/**
	 * Can store 125000 chunks (about 1.2GB)
	 */
	private ChunkHolder chunks = new ChunkHolder();

	/**
	 * Gets a chunk from the holder, if it is not loaded it loads it only if specified
	 * @param x X Chunk Location
	 * @param y	Y Chunk Location
	 * @param z Z Chunk Location
	 * @param load Should the chunk be loaded if it is not already
	 * @return The chunk
	 */
	public Chunk getChunk(int x, int y, int z, Boolean load) {
		if (chunks.chunkLoaded(x, y, z)) {
			return chunks.GetChunk(x, y, z);
		} else if(load) {
			return chunks.LoadChunk(x, y, z, worldLocation);
		}else{
			return null;
		}
	}
	
	/**
	 * Unloads all loaded chunks
	 */
	public void UnloadChunks(){			
		chunks.UnloadChunks();		
	}

	
	
	/**
	 * Generates a chunk if it is not already generated
	 * @param x X Chunk Location
	 * @param y	Y Chunk Location
	 * @param z Z Chunk Location
	 * @return The generated chunk
	 */
	public Chunk GenerateChunk(int x, int y, int z) {
		if (!chunks.chunkLoaded(x, y, z)) {
			return chunks.LoadChunk(x, y, z, worldLocation);
		}
		return null;
	}
	
	/**
	 * Generates chunks
	 * @param xL The number of chunks in the x axis to generate
	 * @param yL The number of chunks in the y axis to generate
	 * @param zL The number of chunks in the z axis to generate
	 */
	public void genTest(int xL, int yL, int zL){
		for(int x = 0; x<xL; x++){
			for(int y = 0; y<yL; y++){
				for(int z = 0; z<zL; z++){
					GenerateChunk(x, y, z);	
					
				}
			}
		}	
	}
	

}
