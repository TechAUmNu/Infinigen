package newModels;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import newTextures.ModelTexture;

public class TexturedPhysicsModel extends TexturedModel {

	private CollisionShape collisionShape;
	
	
	public TexturedPhysicsModel(PhysicsModel model, ModelTexture texture) {
		super(model, texture);
		this.collisionShape = model.getCollisionShape();
	
	}

	public CollisionShape getCollisionShape() {
		return collisionShape;
	}
	
	
	
	
}
