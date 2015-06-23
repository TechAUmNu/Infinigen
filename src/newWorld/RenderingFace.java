package newWorld;

import java.util.ArrayList;

import newMain.Globals;
import newModels.RawModel;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class RenderingFace {
	
	ArrayList<Vector3f> vertices;
	ArrayList<Vector3f> normals;
	ArrayList<Vector2f> textures;
		
	public RenderingFace() {
		super();
		vertices = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		textures = new ArrayList<Vector2f>();		
	}

	public void addFace(int x, int y, int z, int CUBE_LENGTH, Face face){
		int offset = CUBE_LENGTH / 2;
		if(face == Face.Bottom){
			vertices.add(new Vector3f(x + offset, y - offset, z));			
			normals.add(new Vector3f(0, -1, 0));
			textures.add(new Vector2f(0, 0));
			vertices.add(new Vector3f(x - offset, y - offset, z));
			normals.add(new Vector3f(0, -1, 0));	
			textures.add(new Vector2f(1, 0));
			vertices.add(new Vector3f(x - offset, y - offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(0, -1, 0));	
			textures.add(new Vector2f(1, 1));
			vertices.add(new Vector3f(x + offset, y - offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(0, -1, 0));	
			textures.add(new Vector2f(0, 1));			
		}
		if(face == Face.Top){
			vertices.add(new Vector3f(x + offset, y + offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(0, 1, 0));	
			textures.add(new Vector2f(0, 1));
			vertices.add(new Vector3f(x - offset, y + offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(0, 1, 0));	
			textures.add(new Vector2f(1, 1));
			vertices.add(new Vector3f(x - offset, y + offset, z));
			normals.add(new Vector3f(0, 1, 0));	
			textures.add(new Vector2f(1, 0));
			vertices.add(new Vector3f(x + offset, y + offset, z));
			normals.add(new Vector3f(0, 1, 0));	
			textures.add(new Vector2f(0, 0));
		}
		if(face == Face.Front){
			vertices.add(new Vector3f(x + offset, y + offset, z));
			normals.add(new Vector3f(0, 0, 1));	
			textures.add(new Vector2f(0, 1));
			vertices.add(new Vector3f(x - offset, y + offset, z));
			normals.add(new Vector3f(0, 0, 1));	
			textures.add(new Vector2f(1, 1));
			vertices.add(new Vector3f(x - offset, y - offset, z));
			normals.add(new Vector3f(0, 0, 1));	
			textures.add(new Vector2f(1, 0));
			vertices.add(new Vector3f(x + offset, y - offset, z));
			normals.add(new Vector3f(0, 0, 1));	
			textures.add(new Vector2f(0, 0));
		}
		if(face == Face.Back){
			vertices.add(new Vector3f(x + offset, y - offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(0, 0, -1));	
			textures.add(new Vector2f(0, 1));
			vertices.add(new Vector3f(x - offset, y - offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(0, 0, -1));
			textures.add(new Vector2f(1, 1));
			vertices.add(new Vector3f(x - offset, y + offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(0, 0, -1));	
			textures.add(new Vector2f(1, 0));
			vertices.add(new Vector3f(x + offset, y + offset, z - CUBE_LENGTH ));
			normals.add(new Vector3f(0, 0, -1));
			textures.add(new Vector2f(0, 0));
		}
		if(face == Face.Left){
			vertices.add(new Vector3f(x - offset, y + offset, z));
			normals.add(new Vector3f(-1, 0, 0));	
			textures.add(new Vector2f(0, 1));
			vertices.add(new Vector3f(x - offset, y + offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(-1, 0, 0));	
			textures.add(new Vector2f(1, 1));
			vertices.add(new Vector3f(x - offset, y - offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(-1, 0, 0));	
			textures.add(new Vector2f(1, 0));
			vertices.add(new Vector3f(x - offset, y - offset, z  ));
			normals.add(new Vector3f(-1, 0, 0));	
			textures.add(new Vector2f(0, 0));
		}
		if(face == Face.Right){
			vertices.add(new Vector3f(x + offset, y + offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(1, 0, 0));	
			textures.add(new Vector2f(0, 1));
			vertices.add(new Vector3f(x + offset, y + offset, z));
			normals.add(new Vector3f(1, 0, 0));	
			textures.add(new Vector2f(1, 1));
			vertices.add(new Vector3f(x + offset, y - offset, z));
			normals.add(new Vector3f(1, 0, 0));	
			textures.add(new Vector2f(1, 0));
			vertices.add(new Vector3f(x + offset, y - offset, z - CUBE_LENGTH));
			normals.add(new Vector3f(1, 0, 0));	
			textures.add(new Vector2f(0, 0));
		}
		
		
	}
	
	
	public RawModel getModel() {
		ArrayList<Float> positions = new ArrayList<Float>();
		ArrayList<Float> textureCoords = new ArrayList<Float>();
		ArrayList<Float> normalsL = new ArrayList<Float>();
		
		for(Vector3f v : vertices){
			positions.add(v.x);
			positions.add(v.y);
			positions.add(v.z);
		}
		for(Vector3f n : normals){
			normalsL.add(n.x);
			normalsL.add(n.y);
			normalsL.add(n.z);
		}
		for(Vector2f t : textures){
			textureCoords.add(t.x);
			textureCoords.add(t.y);			
		}
		
		float[] positionsArray = new float[positions.size()];
		float[] normalsArray = new float[normalsL.size()];
		float[] texturesArray = new float[textureCoords.size()];
		
		int i = 0;
		for (Float f : positions) {
			positionsArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
		}
		
		i = 0;
		for (Float f : normalsL) {
			normalsArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
		}
		
		i = 0;
		for (Float f : textureCoords) {
			texturesArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
		}
		
		
		
		RawModel model = Globals.getLoader().loadToVAO(positionsArray, texturesArray, normalsArray, vertices.size());
		return model;
	}
	
	
	
}
