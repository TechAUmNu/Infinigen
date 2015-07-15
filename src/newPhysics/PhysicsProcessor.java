package newPhysics;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import newEntities.PhysicsEntity;
import newRendering.DisplayManager;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionWorld.RayResultCallback;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class PhysicsProcessor {

	private DiscreteDynamicsWorld dynamicsWorld;
	private ArrayList<RigidBody> bodies;

	public ArrayList<RigidBody> getBodies() {
		return bodies;
	}

	public void addPhysicsEntity(PhysicsEntity entity) {
		dynamicsWorld.addRigidBody(entity.getBody());
		//System.out.println(entity);
		//System.out.println(entity.getBody());
		//System.out.println(entity.getBody().getAngularVelocity(new Vector3f()));
		bodies.add(entity.getBody());
	}

	public void removePhysicsEntity(PhysicsEntity entity) {
		dynamicsWorld.removeRigidBody(entity.getBody());
		bodies.remove(entity.getBody());
		
	}

	public void simulate() {
		dynamicsWorld.stepSimulation(DisplayManager.getFrameTimeSeconds());
	}
	
	

	public DiscreteDynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;		
	}

	public void setUpPhysics(boolean floor, boolean gravity) {
		bodies = new ArrayList<RigidBody>();
		/**
		 * The object that will roughly find out whether bodies are colliding.
		 */
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

		/**
		 * The object that will accurately find out whether, when, how, and
		 * where bodies are colliding.
		 */
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		/**
		 * The object that will determine what to do after collision.
		 */
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		// Initialise the JBullet world.
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		// Set the gravity to 10 metres per second squared (m/s^2). Gravity
		// affects all bodies with a mass larger than
		// zero.
		
		if(gravity)	dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
		else dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
		// Initialise 'groundShape' to a static plane shape on the origin facing
		// upwards ([0, 1, 0] is the normal).
		// 0.25 metres is an added buffer between the ground and potential
		// colliding bodies, used to prevent the bodies
		// from partially going through the floor. It is also possible to think
		// of this as the plane being lifted 0.25m.
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0f);

		// Initialise 'groundMotionState' to a motion state that simply assigns
		// the origin [0, 0, 0] as the origin of
		// the ground.
		MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0,-1, 0), 1.0f)));
		// Initialise 'groundBodyConstructionInfo' to a value that contains the
		// mass, the motion state, the shape, and the inertia (= resistance to
		// change).
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		// Set the restitution, also known as the bounciness or spring, to 0.25.
		// The restitution may range from 0.0
		// not bouncy) to 1.0 (extremely bouncy).
		groundBodyConstructionInfo.restitution = 0.0f;
		// Initialise 'groundRigidBody', the final variable representing the
		// ground, to a rigid body with the previously
		// assigned construction information.
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		// Add the ground to the JBullet world.
		
		if(floor) dynamicsWorld.addRigidBody(groundRigidBody);

	}

}
