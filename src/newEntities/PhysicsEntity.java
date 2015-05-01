package newEntities;

import newModels.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.shapes.ConvexHullShape;

public class PhysicsEntity extends Entity {

	public PhysicsEntity(TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		GeneratePhysicsObject();
	
	}
	
	
	private void GeneratePhysicsObject(){
		
		
		collisionShape.
	}

}
