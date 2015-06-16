package oldgraphics;

public class ChunkVBO {
	int vertexid;
	int normalid;
	int colorid;
	int visibleFaces;
	int textureid;

	public ChunkVBO(int vertexid, int colorid, int normalid, int visibleFaces, int textureid) {
		this.vertexid = vertexid;
		this.colorid = colorid;
		this.normalid = normalid;
		this.visibleFaces = visibleFaces;
		this.textureid = textureid;
		// System.out.println("VISIBLE : " + visibleFaces);
	}
}
