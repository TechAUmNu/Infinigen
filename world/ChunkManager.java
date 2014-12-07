package world;

//Class to manage loading and rendering of chunks
public class ChunkManager {
	private int viewDistance = 10;
	private int maxLoadedChunks = 100;
	private String worldLocation = "C:\\Testing\\ChunkTesting";
	
	//Can store 8000 chunks (about 1.2GB)
	private ChunkHolder chunks = new ChunkHolder();

	
	public Chunk getChunk(int x, int y, int z, Boolean load) {
		if (chunks.chunkLoaded(x, y, z)) {
			return chunks.GetChunk(x, y, z);
		} else if(load) {
			return chunks.LoadChunk(x, y, z, worldLocation);
		}else{
			return null;
		}
	}
	
	public void UnloadChunks(){
		for(int x = 0; x<20; x++){
			for(int y = 0; y<20; y++){
				for(int z = 0; z<20; z++){
					chunks.UnloadChunk(x, y, z);		
				}
			}
		}	
		
	}

	public void Render(){
		
	}
	public void GenerateChunk(int x, int y, int z) {
		if (!chunks.chunkLoaded(x, y, z)) {
			chunks.LoadChunk(x, y, z, worldLocation);
		}
	}
	
	public void genTest(int i){
		for(int x = 0; x<i; x++){
			for(int y = 0; y<i; y++){
				for(int z = 0; z<i; z++){
					GenerateChunk(x, y, z);					
				}
			}
		}	
	}
	

}
