package world;

import utility.BufferTools;

public class VisibleFaces {
	boolean top;
	boolean bottom;
	boolean front;
	boolean back;
	boolean left;
	boolean right;

	//TODO: make this work
	public float[] genVertexes(int x, int y, int z, int CUBE_LENGTH) {
		float[] vertexes = new float[0];
		if (bottom)
			vertexes = BufferTools.concat(vertexes, genBottom());
		if (top)
			vertexes = BufferTools.concat(vertexes, genTop());
		if (front)
			vertexes = BufferTools.concat(vertexes, genFront());
		if (back)
			vertexes = BufferTools.concat(vertexes, genBack());
		if (left)
			vertexes = BufferTools.concat(vertexes, genLeft());
		if (right)
			vertexes = BufferTools.concat(vertexes, genRight());

		return vertexes;
	}
	
	//TODO: make this work
	public float[] genColors(){
		float[] colors = new float[0];
		if (bottom)
			colors = BufferTools.concat(colors, genBottom());
		if (top)
			colors = BufferTools.concat(colors, genTop());
		if (front)
			colors = BufferTools.concat(colors, genFront());
		if (back)
			colors = BufferTools.concat(colors, genBack());
		if (left)
			colors = BufferTools.concat(colors, genLeft());
		if (right)
			colors = BufferTools.concat(colors, genRight());

		return colors;
	}
	
	//TODO: make this work
	public float[] genNormals(){
		float[] normals = new float[0];
		if (bottom)
			normals = BufferTools.concat(normals, genBottom());
		if (top)
			normals = BufferTools.concat(normals, genTop());
		if (front)
			normals = BufferTools.concat(normals, genFront());
		if (back)
			normals = BufferTools.concat(normals, genBack());
		if (left)
			normals = BufferTools.concat(normals, genLeft());
		if (right)
			normals = BufferTools.concat(normals, genRight());

		return normals;
	}

	private float[] genBottom() {
		return new float[] {
				// BOTTOM QUAD
				x + offset, y - offset, z, x - offset, y - offset, z,
				x - offset, y - offset, z - CUBE_LENGTH, x + offset,
				y - offset, z - CUBE_LENGTH };
	}

	private float[] genTop() {
		return new float[] {
				// TOP
				x + offset, y + offset, z - CUBE_LENGTH, x - offset,
				y + offset, z - CUBE_LENGTH, x - offset, y + offset, z,
				x + offset, y + offset, z };
	}

	private float[] genFront() {
		return new float[] {
				// FRONT QUAD
				x + offset, y + offset, z, x - offset, y + offset, z,
				x - offset, y - offset, z, x + offset, y - offset, z };
	}

	private float[] genBack() {
		return new float[] {
				// BACK QUAD
				x + offset, y - offset, z - CUBE_LENGTH, x - offset,
				y - offset, z - CUBE_LENGTH, x - offset, y + offset,
				z - CUBE_LENGTH, x + offset, y + offset, z - CUBE_LENGTH };
	}

	private float[] genLeft() {
		return new float[] {
				// LEFT QUAD
				x - offset, y + offset, z, x - offset, y + offset,
				z - CUBE_LENGTH, x - offset, y - offset, z - CUBE_LENGTH,
				x - offset, y - offset, z };
	}

	private float[] genRight() {
		return new float[] {
				// RIGHT QUAD
				x + offset, y + offset, z - CUBE_LENGTH, x + offset,
				y + offset, z, x + offset, y - offset, z, x + offset,
				y - offset, z - CUBE_LENGTH };
	}

}
