package newModels;

import com.bulletphysics.collision.shapes.ConvexHullShape;

public class PhysicsModel extends RawModel{

	private ConvexHullShape collisionShape;
	
	public PhysicsModel(int vaoID, int vertexCount, ConvexHullShape collisionShape) {
		super(vaoID, vertexCount);
		this.collisionShape = collisionShape;
	}

	public ConvexHullShape getCollisionShape() {
		return collisionShape;
	}	
	
	
}
