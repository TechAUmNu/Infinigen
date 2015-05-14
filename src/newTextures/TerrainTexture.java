package newTextures;

import javax.vecmath.Vector3f;

public class TerrainTexture {

	private Vector3f textureID;

	public TerrainTexture(Vector3f vector3f) {	
		this.textureID = vector3f;
	}

	public int getTextureID() {
		return (int) textureID.z;
	}
	
	
	
}
