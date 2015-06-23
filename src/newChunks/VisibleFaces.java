package newChunks;

import oldutility.BufferTools;

public class VisibleFaces {
	boolean top;
	boolean bottom;
	boolean front;
	boolean back;
	boolean left;
	boolean right;

	int x, y, z, CUBE_LENGTH, offset;

	public float[] genVertexes(int x, int y, int z, int CUBE_LENGTH) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.CUBE_LENGTH = CUBE_LENGTH;
		offset = CUBE_LENGTH / 2;
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

	public float[] genColors() {
		float[] colors = new float[0];
		if (bottom)
			colors = BufferTools.concat(colors, genBottomColors());
		if (top)
			colors = BufferTools.concat(colors, genTopColors());
		if (front)
			colors = BufferTools.concat(colors, genFrontColors());
		if (back)
			colors = BufferTools.concat(colors, genBackColors());
		if (left)
			colors = BufferTools.concat(colors, genLeftColors());
		if (right)
			colors = BufferTools.concat(colors, genRightColors());

		return colors;
	}

	public float[] genNormals() {
		float[] normals = new float[0];
		if (bottom)
			normals = BufferTools.concat(normals, genBottomNormals());
		if (top)
			normals = BufferTools.concat(normals, genTopNormals());
		if (front)
			normals = BufferTools.concat(normals, genFrontNormals());
		if (back)
			normals = BufferTools.concat(normals, genBackNormals());
		if (left)
			normals = BufferTools.concat(normals, genLeftNormals());
		if (right)
			normals = BufferTools.concat(normals, genRightNormals());

		return normals;
	}

	public float[] genUV() {
		float[] uv = new float[0];
		if (bottom)
			uv = BufferTools.concat(uv, genBottomUVs());
		if (top)
			uv = BufferTools.concat(uv, genTopUVs());
		if (front)
			uv = BufferTools.concat(uv, genFrontUVs());
		if (back)
			uv = BufferTools.concat(uv, genBackUVs());
		if (left)
			uv = BufferTools.concat(uv, genLeftUVs());
		if (right)
			uv = BufferTools.concat(uv, genRightUVs());

		return uv;
	}

	private float[] genBottom() {
		return new float[] {
				// BOTTOM QUAD
				x + offset, y - offset, z, x - offset, y - offset, z, x - offset, y - offset, z - CUBE_LENGTH, x + offset, y - offset, z - CUBE_LENGTH };
	}

	private float[] genTop() {
		return new float[] {
				// TOP
				x + offset, y + offset, z - CUBE_LENGTH, x - offset, y + offset, z - CUBE_LENGTH, x - offset, y + offset, z, x + offset, y + offset, z };
	}

	private float[] genFront() {
		return new float[] {
				// FRONT QUAD
				x + offset, y + offset, z, x - offset, y + offset, z, x - offset, y - offset, z, x + offset, y - offset, z };
	}

	private float[] genBack() {
		return new float[] {
				// BACK QUAD
				x + offset, y - offset, z - CUBE_LENGTH, x - offset, y - offset, z - CUBE_LENGTH, x - offset, y + offset, z - CUBE_LENGTH, x + offset,
				y + offset, z - CUBE_LENGTH };
	}

	private float[] genLeft() {
		return new float[] {
				// LEFT QUAD
				x - offset, y + offset, z, x - offset, y + offset, z - CUBE_LENGTH, x - offset, y - offset, z - CUBE_LENGTH, x - offset, y - offset, z };
	}

	private float[] genRight() {
		return new float[] {
				// RIGHT QUAD
				x + offset, y + offset, z - CUBE_LENGTH, x + offset, y + offset, z, x + offset, y - offset, z, x + offset, y - offset, z - CUBE_LENGTH };
	}

	private float[] genBottomColors() {
		return new float[] {

		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	}

	private float[] genTopColors() {
		return new float[] {

		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	}

	private float[] genFrontColors() {
		return new float[] {

		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	}

	private float[] genBackColors() {
		return new float[] {

		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	}

	private float[] genLeftColors() {
		return new float[] {

		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	}

	private float[] genRightColors() {
		return new float[] {

		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	}

	private float[] genBottomNormals() {
		return new float[] {

		, , , 0, -1, 0 };
	}

	private float[] genTopNormals() {
		return new float[] {

		0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0 };
	}

	private float[] genFrontNormals() {
		return new float[] {

		0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	}

	private float[] genBackNormals() {
		return new float[] {

		0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1 };
	}

	private float[] genLeftNormals() {
		return new float[] {

		-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0 };
	}

	private float[] genRightNormals() {
		return new float[] {

		1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0 };
	}

	private float[] genBottomUVs() {
		return new float[] {

		, , ,  };
	}

	private float[] genTopUVs() {
		return new float[] {

		, , ,  };
	}

	private float[] genFrontUVs() {
		return new float[] {

		, , ,  };
	}

	private float[] genBackUVs() {
		return new float[] {

		, , ,  };
	}

	private float[] genLeftUVs() {
		return new float[] {

		, , ,  };
	}

	private float[] genRightUVs() {
		return new float[] {

		, , , };
	}

}
