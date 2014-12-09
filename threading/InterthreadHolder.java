package threading;

import graphics.Batch;



import org.magicwerk.brownies.collections.GapList;

public class InterthreadHolder {
	private static InterthreadHolder instance;
	
	
	private GapList<Batch> batches;
	

	static {
		setInstance(new InterthreadHolder());
	}	
	
	public void initBatches(){
		batches = new GapList<Batch>();
	}

	//Initialise variables.
	private InterthreadHolder() {		
		initBatches();
	}	

	public GapList<Batch> getBatches() {
		return batches;
	}

	public void addBatch(Batch batch) {
		batches.add(batch);
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
