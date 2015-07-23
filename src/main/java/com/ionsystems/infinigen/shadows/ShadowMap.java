package main.java.com.ionsystems.infinigen.shadows;

import main.java.com.ionsystems.infinigen.utility.MatrixHandler;

public class ShadowMap {

	MatrixHandler depthBiasMatrix;

	public MatrixHandler getDepthBiasMatrix() {
		return depthBiasMatrix;
	}

	public void setDepthBiasMatrix(MatrixHandler depthBiasMatrix) {
		this.depthBiasMatrix = depthBiasMatrix;
	}

	int shadowMapTexture;

	public int getShadowMapTexture() {
		return shadowMapTexture;
	}

	public void setShadowMapTexture(int shadowMapTexture) {
		this.shadowMapTexture = shadowMapTexture;
	}

	public ShadowMap(MatrixHandler depthBiasMatrix, int shadowMapTexture) {
		super();
		this.depthBiasMatrix = depthBiasMatrix;
		this.shadowMapTexture = shadowMapTexture;
	}

}
