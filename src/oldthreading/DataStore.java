package oldthreading;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import oldgraphics.ChunkBatch;
import oldphysics.PhysicsObject;
import oldphysics.PhysicsProcessor;

import org.magicwerk.brownies.collections.GapList;

import com.bulletphysics.dynamics.RigidBody;

public class DataStore {

	/**
	 * This is a singleton data object used for storing common data that needs
	 * to be used in multiple locations. It can be accessed anywhere by any
	 * thread and therefore makes an easy to use way of passing data between
	 * threads without locks.
	 */

	// //////////////////////////////////////////////////////////////
	// // Singleton /////
	// //////////////////

	private static DataStore instance;

	static {
		setInstance(new DataStore());
	}

	public static DataStore getInstance() {
		return instance;
	}

	public static void setInstance(DataStore instance) {
		DataStore.instance = instance;
	}

	// ///////////////////////////////////////////////////////////////
	private DataStore() {
		initGeneral();
		initBatches();
		initPhysics();
	}

	// ///////////////////////////////////////////////////////////////
	// // General ////
	// ///////////////

	private void initGeneral() {
		calcNumberCores();
	}

	private float delta;
	private int cores;

	public float getDelta() {
		return delta;
	}

	public void setDelta(float delta) {
		this.delta = delta;
	}

	public int calcNumberCores() {
		return cores = Runtime.getRuntime().availableProcessors();
	}

	// ///////////////////////////////////////////////////////////////
	// // Batches ////
	// //////////////

	public void initBatches() {
		chunkBatches = new GapList<ChunkBatch>();
	}

	private GapList<ChunkBatch> chunkBatches;

	public GapList<ChunkBatch> getChunkBatches() {
		return chunkBatches;
	}

	public void addChunkBatch(ChunkBatch batch) {
		chunkBatches.add(batch);
	}

	public void removeChunkBatch(ChunkBatch batch) {
		chunkBatches.remove(batch);
	}

	public void resetChunkBatches() {
		chunkBatches.clear();
	}

	// ///////////////////////////////////////////////////////////////
	// // Physics ////
	// ///////////////

	@SuppressWarnings("unchecked")
	private void initPhysics() {
		add = (GapList<RigidBody>[]) new GapList[cores];
		remove = (GapList<RigidBody>[]) new GapList[cores];

		for (int i = 0; i < cores; i++) {
			add[i] = new GapList<RigidBody>();
			remove[i] = new GapList<RigidBody>();
		}

		// Create the barrier for physics
		physicsBarrier = new CyclicBarrier(cores + 1); // The processing threads
														// + the initiator.
		physicsBarrier2 = new CyclicBarrier(cores + 1); // The processing
														// threads + the
														// initiator.

		// Start the physics threads
		for (int id = 0; id < cores; id++) {
			(new Thread(new PhysicsProcessor(physicsBarrier, physicsBarrier2, id))).start();
		}

	}

	private GapList<RigidBody>[] add, remove;
	private CyclicBarrier physicsBarrier, physicsBarrier2;

	public GapList<RigidBody> getAdd(int id) {
		return add[id];
	}

	public GapList<RigidBody> getRemove(int id) {
		return remove[id];
	}

	public void addPhysicsObject(RigidBody object, int id) {
		add[id].add(object);
	}

	public void removePhysicsObject(RigidBody object, int id) {
		remove[id].add(object);
	}

	public void triggerStepSimulateBarrier() {
		try {
			System.out.println("Main Thread waiting at barrier 1");
			physicsBarrier.await();
			// System.out.println("Main Thread Passed barrier");
		} catch (InterruptedException | BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.out.println("Main Thread waiting at barrier 2");
			physicsBarrier2.await();
			// System.out.println("Main Thread Passed barrier");
		} catch (InterruptedException | BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void clearAdd(int id) {
		add[id].clear();
	}

	public void clearRemove(int id) {
		remove[id].clear();
	}

}
