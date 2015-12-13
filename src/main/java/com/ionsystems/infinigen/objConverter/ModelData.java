package main.java.com.ionsystems.infinigen.objConverter;

import java.util.List;

public class ModelData {

	private float[] vertices;
	private List<Vertex> verticesList;
	private float[] textureCoords;
	private float[] normals;
	private int[] indices;
	private float furthestPoint;

	public ModelData(float[] vertices, List<Vertex> verticesList, float[] textureCoords, float[] normals, int[] indices, float furthestPoint) {
		this.vertices = vertices;
		this.verticesList = verticesList;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.furthestPoint = furthestPoint;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public float getFurthestPoint() {
		return furthestPoint;
	}

	public List<Vertex> getVerticesList() {
		return verticesList;
	}

}