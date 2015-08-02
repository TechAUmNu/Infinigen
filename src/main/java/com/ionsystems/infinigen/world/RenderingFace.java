package main.java.com.ionsystems.infinigen.world;

import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.RawModel;

import org.lwjgl.util.vector.Vector2f;

import javax.vecmath.Vector3f;

import com.bulletphysics.util.ObjectArrayList;

public class RenderingFace {
	
	ObjectArrayList<Vector3f> vertices;
	ArrayList<Vector3f> normals;
	ArrayList<Vector2f> textures;
	ArrayList<Integer> indicies;
	
	int count;
		
	public RenderingFace() {
		super();
		count = -1;
		vertices = new ObjectArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		textures = new ArrayList<Vector2f>();
		indicies = new ArrayList<Integer>();
		
	}

	public void addFace(float x, float y, float z, float CUBE_LENGTH, Face face){
		float offset = CUBE_LENGTH / 2f;
		x= x*CUBE_LENGTH;
		y=y*CUBE_LENGTH ;
		z=z*CUBE_LENGTH ;
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
			addIndicies();
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
			addIndicies();
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
			addIndicies();
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
			addIndicies();
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
			addIndicies();
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
			addIndicies();
		}
		
		
	}

	private void addIndicies() {
		addIndex(1);
		addIndex(2);
		addIndex(3);
		addIndex(1);
		addIndex(3);
		addIndex(4);
		nextFace();
	}

	private void addIndex(int vertex) {
		indicies.add(count + vertex);
	}
	
	private void nextFace(){
		count += 4;
	}
	
	
	public PhysicsModel getModel() {
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
		int[] indiciesArray = new int[indicies.size()];
		
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
		
		i = 0;
		for (int f : indicies) {
			indiciesArray[i++] = f; // Or whatever default you want.
		}
		
		
		
		PhysicsModel model = Globals.getLoader().loadChunkToVAOWithGeneratedPhysics(positionsArray, vertices,  texturesArray, normalsArray, indiciesArray);
		model.generateWorldRigidBody();
		return model;
	}
	
	
	
}
