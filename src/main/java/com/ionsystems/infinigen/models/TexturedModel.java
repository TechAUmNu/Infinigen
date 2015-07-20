package main.java.com.ionsystems.infinigen.models;

import java.io.Serializable;

import main.java.com.ionsystems.infinigen.textures.ModelTexture;

public class TexturedModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7077715080759594243L;
	private RawModel rawModel;
	private ModelTexture texture;

	public TexturedModel(RawModel model, ModelTexture texture) {
		this.rawModel = model;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

}
