package oldphysics;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.xml.crypto.Data;

import oldthreading.DataStore;

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

public class PhysicsManager {
	
	private static PhysicsManager instance;

	static {
		setInstance(new PhysicsManager());
	}

	// Initialise variables.
	private PhysicsManager() {}

	public static PhysicsManager getInstance() {
		return instance;
	}

	public static void setInstance(PhysicsManager instance) {
		PhysicsManager.instance = instance;
	}
	

	public void stepSimulate(float delta){		
		DataStore.getInstance().triggerStepSimulateBarrier();
	}
	
	

	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}


	public void addPhysicsObject(RigidBody p, int id) {
		DataStore.getInstance().addPhysicsObject(p, id);		
	}

	

}
