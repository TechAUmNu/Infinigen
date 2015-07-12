package newNetworking;

import java.io.Serializable;

import newWorld.Block;

public class ChunkData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5745827877308017669L;

	
	public Block[][][] blocks;
	public int x, y, z;
	public float blockSize;
	public int size;
	
}
