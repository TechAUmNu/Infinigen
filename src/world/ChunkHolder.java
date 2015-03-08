package world;

import java.util.HashMap;

import org.magicwerk.brownies.collections.GapList;

import world.Block.BlockType;


public class ChunkHolder {
	
	
	
	
	private HashMap<ChunkID,Chunk> chunks = new HashMap<ChunkID,Chunk>();
	public GapList<ChunkID> loadedChunks = new GapList<ChunkID>();
	
	
	/**
	 * Checks if a chunk is loaded
	 * @param x X Chunk location
	 * @param y Y Chunk location
	 * @param z Z Chunk location
	 * @return If the chunk is loaded
	 */
	public Boolean chunkLoaded(int x, int y, int z){
		if(chunks.get(new ChunkID(x,y,z)) != null)	
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
		return chunks.get(new ChunkID(x,y,z));
	}		
	
	/**
	 * Loads a chunk
	 * @param x X Chunk location
	 * @param y Y Chunk location
	 * @param z Z Chunk location
	 * @param worldLocation Location of the world folder
	 * @return The loaded chunk
	 */
	public Chunk LoadChunk(int x, int y, int z, String worldLocation, BlockType type){
		chunks.put(new ChunkID(x,y,z), new ChunkLoader(worldLocation).Load(x, y, z, type));	
		//System.out.println("Adding chunk to loaded chunks");
		loadedChunks.add(new ChunkID(x,y,z));
		return chunks.get(new ChunkID(x,y,z));
	}

	/**
	 * Unloads a specific chunk
	 * @param x X chunk location
	 * @param y Y chunk location
	 * @param z Z chunk location
	 */
	public void UnloadChunk(int x, int y, int z){
		new ChunkLoader("").Unload(chunks.get(new ChunkID(x,y,z)));				
		//System.out.println("Removing chunk from loaded chunks");
		loadedChunks.remove(new ChunkID(x,y,z));
		chunks.remove(new ChunkID(x,y,z));
	}
	
	/**
	 * Unloads all loaded chunks
	 */
	public void UnloadChunks(){
		//System.out.println("UNLOAD CHUNKS 2");
		ChunkLoader cl = new ChunkLoader("");
		for(ChunkID c : loadedChunks){
			cl.Unload(chunks.get(c));
			chunks.remove(c);
		}	
		loadedChunks.clear();
	}

}
