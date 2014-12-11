package threading;

import graphics.ChunkBatch;



import org.magicwerk.brownies.collections.GapList;

public class InterthreadHolder {
	private static InterthreadHolder instance;
	
	
	private GapList<ChunkBatch> batches;
	

	static {
		setInstance(new InterthreadHolder());
	}	
	
	public void initBatches(){
		batches = new GapList<ChunkBatch>();
	}

	//Initialise variables.
	private InterthreadHolder() {		
		initBatches();
	}	

	public GapList<ChunkBatch> getBatches() {
		return batches;
	}

	public void addBatch(ChunkBatch batch) {
		batches.add(batch);
	}
	
	public void removeBatch(ChunkBatch batch){
		batches.remove(batch);
	}
	public void resetBatches(){
		batches.clear();
	}

	public static InterthreadHolder getInstance() {
		return instance;
	}

	public static void setInstance(InterthreadHolder instance) {
		InterthreadHolder.instance = instance;
	}



	
	
}
