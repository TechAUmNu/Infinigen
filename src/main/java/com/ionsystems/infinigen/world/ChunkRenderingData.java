package main.java.com.ionsystems.infinigen.world;

public class ChunkRenderingData {
	public float[] positions;
	public float[] normals;
	public int[] indicies;
	public Chunk c;

	public ChunkRenderingData(float[] positions, int[] int_indicies, float[] normals, Chunk c) {
		super();
		this.positions = positions;
		this.indicies = int_indicies;
		this.normals = normals;
		this.c = c;
	}

}
