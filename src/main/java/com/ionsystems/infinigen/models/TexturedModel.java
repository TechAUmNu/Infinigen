package main.java.com.ionsystems.infinigen.models;

import java.io.Serializable;

import main.java.com.ionsystems.infinigen.textures.ModelTexture;

public class TexturedModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7077715080759594243L;
	private PhysicsModel physicsModel;
	private ModelTexture texture;

	public TexturedModel(PhysicsModel physicsModel, ModelTexture texture) {
		this.physicsModel = physicsModel;
		this.texture = texture;
	}

	public PhysicsModel getPhysicsModel() {
		return physicsModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

}
