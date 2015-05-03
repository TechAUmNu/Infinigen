package newModels;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;

public class PhysicsModel extends RawModel{

	private CollisionShape collisionShape;

	
	public PhysicsModel(int vaoID, int vertexCount, CollisionShape cs) {
		super(vaoID, vertexCount);
		this.collisionShape = cs;
		
	}

	public CollisionShape getCollisionShape() {
		return collisionShape;
	}

	
	
	
}
