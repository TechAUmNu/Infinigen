package world;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;


public class Chunk {

	static final int CHUNK_SIZE = 128;
	static final int CUBE_LENGTH = 2;
	private Block[][][] Blocks;
	private int VBOVertexHandle;
	private int VBOColorHandle;
	private int StartX, StartY, StartZ;
	
	
	
	public void Render() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOVertexHandle);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOColorHandle);
		GL11.glColorPointer(3, GL11.GL_FLOAT, 0, 0L);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE
				* CHUNK_SIZE * 24);
	}
	
	
	public void Update() {

	}
	
	
	public void Save(){
		
	}
	
	public void Load(){
		
	}
	
	public Chunk(){
		
	}
	
	public Chunk()
	
}
