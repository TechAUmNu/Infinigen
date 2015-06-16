package newWorld;

/**
 * Handles loading of chunks from file or network or just adding a blank one
 * 
 * @author Euan
 *
 */
public class ChunkLoader {

	/**
	 * Method to create a chunk
	 */
	public Chunk getChunk(int x, int y, int z, int size) {
		return new Chunk(x, y, z, size);
	}

}
