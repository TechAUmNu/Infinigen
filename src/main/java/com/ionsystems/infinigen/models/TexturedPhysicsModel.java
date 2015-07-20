package main.java.com.ionsystems.infinigen.models;

import java.io.Serializable;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

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
