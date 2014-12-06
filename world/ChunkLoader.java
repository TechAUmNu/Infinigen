package world;

import java.io.File;

//Loader for chunks, which executes on a new thread
public class ChunkLoader {
	
	
	private String worldLocation;

	public ChunkLoader(String worldLocation) {
		this.worldLocation = worldLocation;
		// TODO Auto-generated constructor stub
	}
	
	public Chunk Load(int x, int y, int z){
		Chunk c = new Chunk(x, y, z, worldLocation);
		c.Load();
		return c;
	}
	
	public void Save(){
		//TODO: save chunk
	}

}
