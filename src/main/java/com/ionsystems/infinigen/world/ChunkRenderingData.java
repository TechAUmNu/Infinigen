package main.java.com.ionsystems.infinigen.world;

public class ChunkRenderingData {
	public float[] positions;
	public float[] textureCoords;
	public float[] normals;
	public Chunk c;
	public ChunkRenderingData(float[] positions, float[] textureCoords, float[] normals, Chunk c) {
		super();
		this.positions = positions;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.c = c;
	}
	
	
}
