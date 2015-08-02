package main.java.com.ionsystems.infinigen.models;

import java.io.Serializable;
import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class PhysicsModel extends RawModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6033176731940245791L;
	private CollisionShape collisionShape;
	private RigidBody body;

	public PhysicsModel(int vaoID, ArrayList<Integer> vboIDs, int vertexCount, CollisionShape cs) {
		super(vaoID, vboIDs, vertexCount);
		this.collisionShape = cs;
	}
	
	public PhysicsModel(int vaoID, ArrayList<Integer> vboIDs, int vertexCount) {
		super(vaoID, vboIDs, vertexCount);		
	}

	public CollisionShape getCollisionShape() {
		return collisionShape;
	}
	
	public RigidBody getBody(){
		return body;
	}
	
	public void generateWorldRigidBody(){
		MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0,-1, 0), 1.0f)));
		// Initialise 'groundBodyConstructionInfo' to a value that contains the
		// mass, the motion state, the shape, and the inertia (= resistance to
		// change).
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, collisionShape, new Vector3f(0, 0, 0));
		// Set the restitution, also known as the bounciness or spring, to 0.25.
		// The restitution may range from 0.0
		// not bouncy) to 1.0 (extremely bouncy).
		groundBodyConstructionInfo.restitution = 0.0f;
		// Initialise 'groundRigidBody', the final variable representing the
		// ground, to a rigid body with the previously
		// assigned construction information.
		body = new RigidBody(groundBodyConstructionInfo);
	}

}
