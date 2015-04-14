package threading;

import graphics.ChunkBatch;





import org.magicwerk.brownies.collections.GapList;

public class InterthreadHolder {
	private static InterthreadHolder instance;
	
	
	private GapList<ChunkBatch> chunkBatches;
	
	

	static {
		setInstance(new InterthreadHolder());
	}	
	
	public void initBatches(){
		chunkBatches = new GapList<ChunkBatch>();
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

	

	
}
