package graphics;

public class ChunkVBO {
	int vertexid;
	int normalid;
	int colorid;	
	int visibleBlocks;
	
	
	
	public ChunkVBO(int vertexid, int colorid, int normalid, int visibleBlocks){
		this.vertexid = vertexid;		
		this.colorid = colorid;		
		this.normalid = normalid;
		this.visibleBlocks = visibleBlocks;
		System.out.println("VISIBLE : " + visibleBlocks);
	}
}
