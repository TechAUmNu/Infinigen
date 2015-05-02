package newModels;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import newTextures.ModelTexture;

public class TexturedPhysicsModel extends TexturedModel {

	private ConvexHullShape collisionShape;
	
	public TexturedPhysicsModel(PhysicsModel model, ModelTexture texture) {
		super(model, texture);
		this.collisionShape = model.getCollisionShape();
	}

	public ConvexHullShape getCollisionShape() {
		return collisionShape;
	}
	
	
	
}
