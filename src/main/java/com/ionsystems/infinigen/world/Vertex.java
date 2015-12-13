package main.java.com.ionsystems.infinigen.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	public int id;
	public Vector3f position;
	public Vector3f normal = new Vector3f();
	public ArrayList<Vector3f> normals;

	public Vertex(int id, Vector3f position, Vector3f normal) {
		this.id = id;
		this.position = position;
		normals = new ArrayList<Vector3f>();
		normals.add(normal);
	}

	public void addNormal(Vector3f normal) {
		normals.add(normal);
	}

	public void averageNormals() {
		for (Vector3f n : normals) {
			normal = Vector3f.add(normal, n, null);
		}
		normal.x = normal.x / normals.size();
		normal.y = normal.y / normals.size();
		normal.z = normal.z / normals.size();
		// normal.normalise();
	}

}
