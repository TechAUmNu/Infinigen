package main.java.com.ionsystems.infinigen.world;

import org.lwjgl.util.vector.Vector3f;

/**
 * Used for identifying a vertex in a hashmap.
 * 
 * @author Euan
 *
 */
public class VertexID {
	public Vector3f position;

	public VertexID(Vector3f position) {
		this.position = position;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(position.x);
		result = prime * result + Float.floatToIntBits(position.y);
		result = prime * result + Float.floatToIntBits(position.z);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VertexID other = (VertexID) obj;
		if (position.x != other.position.x) {
			return false;
		}
		if (position.y != other.position.y) {
			return false;
		}
		if (position.z != other.position.z) {
			return false;
		}
		return true;
	}
}
