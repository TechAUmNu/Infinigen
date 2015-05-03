package newEntities;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import newModels.TexturedPhysicsModel;
import newPhysics.PhysicsProcessor;
import newUtility.Maths;







import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class PhysicsEntity extends Entity {

	protected TexturedPhysicsModel model;
	protected RigidBody body;
	protected boolean physicsBody = false;
		
	public PhysicsEntity(TexturedPhysicsModel model, org.lwjgl.util.vector.Vector3f position, float rotX,
			float rotY, float rotZ, float scale, float mass, PhysicsProcessor processor) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.model = model;		
		generateRigidBody(mass, position, rotX, rotY, rotZ, scale);
		processor.addPhysicsEntity(this);
	}

	public TexturedPhysicsModel getModel() {
		return model;
	}
	
	private void generateRigidBody(float mass, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale){
		
		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(rotX, rotY, rotZ, scale), new Vector3f(position.x, position.y , position.z), scale)));
		Vector3f inertia = new Vector3f(0, 0, 0);
		CollisionShape shape = model.getCollisionShape();
        shape.calculateLocalInertia(1.0f, inertia);
         
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1, motionState, shape, inertia);
        constructionInfo.restitution = 0f;
        RigidBody rigidBody = new RigidBody(constructionInfo);
         
		body = rigidBody;
		physicsBody = true;
		
	}
	
	public boolean isPhysicsBody(){
		return physicsBody;
	}
	
	
	public float[] updateTransformationMatrixFloat(){	
		
		Transform transform = body.getMotionState().getWorldTransform(new Transform());		
		float[] matrix = new float[16];
		transform.getOpenGLMatrix(matrix);
		return matrix;
	}
	 

	public RigidBody getBody() {
		return body;
	}
	
	
}
