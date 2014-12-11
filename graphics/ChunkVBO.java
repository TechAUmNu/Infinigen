package graphics;

public class ChunkVBO {
	int vertexid;
	int normalid;
	int colorid;
	int noVertices;
	int noNormals;
	int noColors;
	int visibleBlocks;
	
	
	
	public ChunkVBO(int vertexid, int colorid, int noVertices, int noColors, int visibleBlocks){
		this.vertexid = vertexid;
		//this.normalid = normalid;
		this.colorid = colorid;
		this.noVertices = noVertices;
		//this.noNormals = noNormals;
		this.noColors = noColors;
		this.visibleBlocks = visibleBlocks;
	}
}
