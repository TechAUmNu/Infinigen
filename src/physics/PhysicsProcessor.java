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

import entities.Constraint;
import threading.DataStore;

public class PhysicsProcessor implements Runnable {

	
	CyclicBarrier barrier, barrier2;

	int id;
	
	int numberEntities;

	public PhysicsProcessor(CyclicBarrier barrier, CyclicBarrier barrier2, int id) {
		this.barrier = barrier;
		this.barrier2 = barrier2;
		this.id = id;
		//
		//etUpPhysics();
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

	

	

}
