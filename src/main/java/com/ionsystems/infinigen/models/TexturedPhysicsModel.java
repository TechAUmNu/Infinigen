package main.java.com.ionsystems.infinigen.models;

import java.io.Serializable;

import com.bulletphysics.collision.shapes.CollisionShape;
import main.java.com.ionsystems.infinigen.textures.ModelTexture;

public class TexturedPhysicsModel extends TexturedModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6825399073509573649L;
	private CollisionShape collisionShape;

	public TexturedPhysicsModel(PhysicsModel model, ModelTexture texture) {
		super(model, texture);
		this.collisionShape = model.getCollisionShape();

	}

	public CollisionShape getCollisionShape() {
		return collisionShape;
	}

}
