package main.java.com.ionsystems.infinigen.world;

import java.io.Serializable;

import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.utility.Utils;

public class ChunkRenderingData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4339934303631815156L;
	public float[] positions;
	public float[] normals;
	public int[] indicies;
	private boolean renderable;
	private PhysicsModel model;
	
	

	public ChunkRenderingData(float[] positions, int[] int_indicies, float[] normals) {
		super();
		this.positions = positions;
		this.indicies = int_indicies;
		this.normals = normals;		
	}
	
	public void reducePrecision(){
		positions = Utils.reduceFloatPrecision(positions);
		normals = Utils.reduceFloatPrecision(normals);
	}

	
	public void setRenderable(boolean b) {
		renderable = b;
	}

	public boolean isRenderable() {
		return renderable;
	}


	public PhysicsModel getModel() {
		return model;
	}


	public void setModel(PhysicsModel model) {
		this.model = model;
	}

	
}
