package main.java.com.ionsystems.infinigen.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.physics.PhysicsProcessor;
import main.java.com.ionsystems.infinigen.unitBuilder.LocationID;
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
	public LocationID center;
	public List<LocationID> gridPoints; //this is the list of all the "cubes" that a large object takes up

	
	
	public PhysicsEntity(TexturedPhysicsModel model, org.lwjgl.util.vector.Vector3f position, float rotX, float rotY, float rotZ, float scale, float mass,
			PhysicsProcessor processor, int xScale, int yScale, int zScale) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.model = model;
		generateCubeGrid(position, xScale, yScale, zScale);
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
	
	private void generateCubeGrid(org.lwjgl.util.vector.Vector3f position, int xScale, int yScale, int zScale){
		/*this takes in the center and a size in the form XxYxZ
		 we take the x and if it is evan  make (x -1) / 2
		 	if odd a just the x of the center by half a cube down and make x/2 above and x/2 - 1 below
		 	
		 for y and z take the existing list and do the above to each point in the list
		 */
		System.out.println(position);
		gridPoints = new ArrayList<LocationID>();
		gridPoints.add(new LocationID(position.x, position.y, position.z)); //add the center of mass
		center = new LocationID(position.x, position.y, position.z);
		if(xScale != 1){
			if ( (xScale & 1) == 0 ){ //even
				
			}
			else{ //odd
				
			}
		}
		
		if(yScale != 1){
			if ( (xScale & 1) == 0 ){ //even
				
			}
			else{ //odd
				
			}		
				}
		if(zScale != 1){
			if ( (xScale & 1) == 0 ){ //even
				
			}
			else{ //odd
				
			}
		}
		
	}
	
	public boolean entityContainsPoint(javax.vecmath.Vector3f lock){
		for(LocationID i: gridPoints){
			if(i.x == lock.x && i.y == lock.y && i.z == lock.z)return true;
		}
		return false;
	}
	
	

}
