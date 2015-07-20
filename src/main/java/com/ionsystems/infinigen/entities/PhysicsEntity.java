package main.java.com.ionsystems.infinigen.entities;

import java.io.Serializable;
import java.util.UUID;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.physics.PhysicsProcessor;
import main.java.com.ionsystems.infinigen.utility.Maths;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class PhysicsEntity extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6265237686808713962L;
	protected TexturedPhysicsModel model;
	protected RigidBody body;
	protected boolean physicsBody = false;
	protected boolean highlight = false;

	
	
	public PhysicsEntity(TexturedPhysicsModel model, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale, float mass,
			PhysicsProcessor processor) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.model = model;
		generateRigidBody(mass, position, rotX, rotY, rotZ, scale);
		processor.addPhysicsEntity(this);
		body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
	}

	public TexturedPhysicsModel getModel() {
		return model;
	}

	private void generateRigidBody(float mass, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale) {

		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(rotX, rotY, rotZ, scale), new Vector3f(position.x,
				position.y, position.z), scale)));
		Vector3f inertia = new Vector3f(0, 0, 0);
		CollisionShape shape = model.getCollisionShape();
		shape.calculateLocalInertia(mass, inertia);

		RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(mass, motionState, shape, inertia);
		constructionInfo.restitution = 0f;
		RigidBody rigidBody = new RigidBody(constructionInfo);
		rigidBody.bodyIdHash = UUID.randomUUID();
		rigidBody.clientID = Globals.getClientID();
		
		System.out.println("Body ID HASH " + rigidBody.bodyIdHash);
		body = rigidBody;
		physicsBody = true;		
	}
	

	public boolean isPhysicsBody() {
		return physicsBody;
	}

	public float[] updateTransformationMatrixFloat() {

		Transform transform = body.getMotionState().getWorldTransform(new Transform());
		float[] matrix = new float[16];
		transform.getOpenGLMatrix(matrix);
		return matrix;
	}

	public RigidBody getBody() {
		return body;
	}

	public void highlight(boolean highlight) {
		this.highlight = highlight;		
	}

	public boolean isHighlighted() {
		return highlight;
	}

}
