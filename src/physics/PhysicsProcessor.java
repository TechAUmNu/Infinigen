package physics;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.magicwerk.brownies.collections.GapList;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
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

import threading.DataStore;

public class PhysicsProcessor implements Runnable {

	DiscreteDynamicsWorld dynamicsWorld;
	CyclicBarrier barrier, barrier2;

	int id;
	
	int numberEntities;

	public PhysicsProcessor(CyclicBarrier barrier, CyclicBarrier barrier2, int id) {
		this.barrier = barrier;
		this.barrier2 = barrier2;
		this.id = id;
		
		setUpPhysics();
		numberEntities = 0;
		
		System.out.println("Physics Thread " + id + " Started Sucessfully");
	}

	@Override
	public void run() {
		
		// Wait for barrier
		// Get latest delta
		// Add / remove entities
		// Simulate for delta time
		// loop
		
		while (true) {
			try {
				System.out.println("Physics Thread " + id + " waiting for barrier to be hit");
				barrier.await(); // wait for the barrier to be hit by the main
									// thread.
				
				System.out.println("Physics Thread " + id + " Executing");
			} catch (InterruptedException ex) {
			} catch (BrokenBarrierException ex) {
			}

			float delta = DataStore.getInstance().getDelta();

			GapList<RigidBody> remove = DataStore.getInstance().getRemove(
					id);
			GapList<RigidBody> add = DataStore.getInstance().getAdd(id);
			

			for (RigidBody body : remove) {
				if(body != null){
					dynamicsWorld.removeRigidBody(body);
					numberEntities--;
				}
			}
			for (RigidBody body : add) {
				if(body != null){
					dynamicsWorld.addRigidBody(body);
					numberEntities++;
				}
			}
			DataStore.getInstance().clearAdd(id);
			DataStore.getInstance().clearRemove(id);
			System.out.println((float)delta / (float) 1000f);
			dynamicsWorld.stepSimulation((float)delta / (float) 1000f); // Need to convert to
														// seconds.
			System.out.println("Physics Thread " + id + " Processing: " + numberEntities);
			
			try {
				System.out.println("Physics Thread " + id + " Finished");
				barrier2.await(); // wait for the barrier to be hit by the main
									// thread.
				
				//System.out.println("Physics Thread " + id + " Executing");
			} catch (InterruptedException ex) {
			} catch (BrokenBarrierException ex) {
			}
			
		}

	}

	

	public void setUpPhysics() {
		/**
		 * The object that will roughly find out whether bodies are colliding.
		 */
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		/**
		 * The object that will accurately find out whether, when, how, and
		 * where bodies are colliding.
		 */
		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collisionConfiguration);
		/**
		 * The object that will determine what to do after collision.
		 */
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		// Initialise the JBullet world.
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase,
				solver, collisionConfiguration);
		// Set the gravity to 10 metres per second squared (m/s^2). Gravity
		// affects all bodies with a mass larger than
		// zero.
		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
		// Initialise 'groundShape' to a static plane shape on the origin facing
		// upwards ([0, 1, 0] is the normal).
		// 0.25 metres is an added buffer between the ground and potential
		// colliding bodies, used to prevent the bodies
		// from partially going through the floor. It is also possible to think
		// of this as the plane being lifted 0.25m.
		CollisionShape groundShape = new StaticPlaneShape(
				new Vector3f(0, 1, 0), 31f);

		// Initialise 'groundMotionState' to a motion state that simply assigns
		// the origin [0, 0, 0] as the origin of
		// the ground.
		MotionState groundMotionState = new DefaultMotionState(new Transform(
				new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0),
						1.0f)));
		// Initialise 'groundBodyConstructionInfo' to a value that contains the
		// mass, the motion state, the shape, and the inertia (= resistance to
		// change).
		RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(
				0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		// Set the restitution, also known as the bounciness or spring, to 0.25.
		// The restitution may range from 0.0
		// not bouncy) to 1.0 (extremely bouncy).
		groundBodyConstructionInfo.restitution = 0.6f;
		// Initialise 'groundRigidBody', the final variable representing the
		// ground, to a rigid body with the previously
		// assigned construction information.
		RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
		// Add the ground to the JBullet world.
		dynamicsWorld.addRigidBody(groundRigidBody);

	}

}
