package main.java.com.ionsystems.infinigen.world;

public class NetworkChunkRenderingData {
	ChunkRenderingData crd;
	public int x, y, z;
	public float blockSize;
	public int size;
	
	public NetworkChunkRenderingData(ChunkRenderingData crd, int x, int y, int z, float blockSize, int size) {
		super();
		this.crd = crd;		
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockSize = blockSize;
		this.size = size;
	}
	

	

}
