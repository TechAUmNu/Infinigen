package main.java.com.ionsystems.infinigen.world;

import java.io.Serializable;

public class NetworkChunkData implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2877372735073111754L;
	public byte[] uncompressedData;
	public int x, y, z;
	public float blockSize;
	public int size;
	public ChunkID chunkID;
	
	public NetworkChunkData(byte[] uncompressedData, int x, int y, int z, float blockSize, int size, ChunkID id) {
		super();
		this.uncompressedData = uncompressedData;		
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockSize = blockSize;
		this.size = size;
		this.chunkID = id;
	}
	
	protected NetworkChunkData clone() {
        try {
			return (NetworkChunkData) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
	
	public void print(){
		System.out.println("Server: NCD data:  " + blockSize + "  " + size  +  "  " + x + "  " + y + "  " + z + "  " + uncompressedData.length );
	}
	

}
