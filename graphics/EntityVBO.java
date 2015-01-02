package graphics;

public class EntityVBO {
	int vertexid;
	int normalid;
	int colorid;	
	int visibleFaces;
	
	
	
	public EntityVBO(int vertexid, int colorid, int normalid, int visibleFaces){
		this.vertexid = vertexid;		
		this.colorid = colorid;		
		this.normalid = normalid;
		this.visibleFaces = visibleFaces;
		//System.out.println("VISIBLE : " + visibleFaces);
	}
}
