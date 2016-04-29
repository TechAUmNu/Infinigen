package main.java.com.ionsystems.infinigen.world;

import java.io.Serializable;

public class NetworkChunkRenderingData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2877372735073111754L;
	public ChunkRenderingData crd;
	public int x, y, z;
	public float blockSize;
	public int size;
	public ChunkID chunkID;
	
	public NetworkChunkRenderingData(ChunkRenderingData crd, int x, int y, int z, float blockSize, int size, ChunkID id) {
		super();
		this.crd = crd;		
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockSize = blockSize;
		this.size = size;
		this.chunkID = id;
	}
	

	

}
