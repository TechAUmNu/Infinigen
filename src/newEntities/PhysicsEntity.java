package newEntities;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import newModels.TexturedPhysicsModel;
import newUtility.Maths;




import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class PhysicsEntity extends Entity {

	protected TexturedPhysicsModel model;
	protected RigidBody body;
		
	public PhysicsEntity(TexturedPhysicsModel model, org.lwjgl.util.vector.Vector3f position, float rotX,
			float rotY, float rotZ, float scale, float mass) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.model = model;		
		generateRigidBody(mass, position, rotX, rotY, rotZ, scale);
	}

	public TexturedPhysicsModel getModel() {
		return model;
	}
	
	private void generateRigidBody(float mass, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale){
		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(rotX, rotY, rotZ, scale), new Vector3f(position.x, position.y, position.z), scale)));
		body = new RigidBody(mass, motionState, model.getCollisionShape());
	}
	
	@Override
	public org.lwjgl.util.vector.Matrix4f updateTransformationMatrix(){	
		return Maths.convertMatrix(body.getMotionState().getWorldTransform(null).getMatrix(null));
	}
	
	
}
