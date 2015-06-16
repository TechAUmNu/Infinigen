package oldworld;

import java.io.File;

import oldworld.Block.BlockType;

//Loader for chunks, which executes on a new thread
public class ChunkLoader {

	private String worldLocation;

	public ChunkLoader(String worldLocation) {
		this.worldLocation = worldLocation;
		// TODO Auto-generated constructor stub
	}

	public Chunk Load(int x, int y, int z, BlockType type) {
		Chunk c = new Chunk(x, y, z, worldLocation, type);
		c.Load();
		return c;
	}

	public void Unload(Chunk c) {
		// System.out.println("UNLOAD");
		c.Save();
		c.CleanUp();
	}

}
