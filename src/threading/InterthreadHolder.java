package threading;

import graphics.ChunkBatch;



import graphics.EntityBatch;

import org.magicwerk.brownies.collections.GapList;

public class InterthreadHolder {
	private static InterthreadHolder instance;
	
	
	private GapList<ChunkBatch> chunkBatches;
	private GapList<EntityBatch> entityBatches;
	

	static {
		setInstance(new InterthreadHolder());
	}	
	
	public void initBatches(){
		chunkBatches = new GapList<ChunkBatch>();
		entityBatches = new GapList<EntityBatch>();
	}

	//Initialise variables.
	private InterthreadHolder() {		
		initBatches();
	}	

	public GapList<ChunkBatch> getChunkBatches() {
		return chunkBatches;
	}

	public void addChunkBatch(ChunkBatch batch) {
		chunkBatches.add(batch);
	}
	
	public void removeChunkBatch(ChunkBatch batch){
		chunkBatches.remove(batch);
	}
	public void resetChunkBatches(){
		chunkBatches.clear();
	}

	public static InterthreadHolder getInstance() {
		return instance;
	}

	public static void setInstance(InterthreadHolder instance) {
		InterthreadHolder.instance = instance;
	}

	public GapList<EntityBatch> getEntityBatches() {
		return entityBatches;
	}

	public void addEntityBatch(EntityBatch batch) {
		entityBatches.add(batch);
	}
	
	public void removeEntityBatch(EntityBatch batch){
		entityBatches.remove(batch);
	}
	public void resetEntityBatches(){
		entityBatches.clear();
	}
	
	
}
