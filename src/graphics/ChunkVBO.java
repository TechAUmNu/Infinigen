package graphics;

public class ChunkVBO {
	int vertexid;
	int normalid;
	int colorid;	
	int visibleFaces;
	
	
	
	public ChunkVBO(int vertexid, int colorid, int normalid, int visibleFaces){
		this.vertexid = vertexid;		
		this.colorid = colorid;		
		this.normalid = normalid;
		this.visibleFaces = visibleFaces;
		//System.out.println("VISIBLE : " + visibleFaces);
	}
}
