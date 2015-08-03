package main.java.com.ionsystems.infinigen.unitBuilder;

/**
 * Used for identifying a chunk.
 * 
 * @author Euan
 *
 */
public class LocationID {
	public float x, y, z;

	public LocationID(float x2, float y2, float z2) {
		this.x = x2;
		this.y = y2;
		this.z = z2;
	}

	@Override
	public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Float.floatToIntBits(x);
	result = prime * result + Float.floatToIntBits(y);
	result = prime * result + Float.floatToIntBits(z);
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
		LocationID other = (LocationID) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}
}
