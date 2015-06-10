package newWorld;


/**
 *  A chunk
 * @author Euan
 *
 */
public class Chunk {
	
	Block[][][] blocks;
	int x, y, z, size;
	
	
	public Chunk(int x, int y, int z, int size) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		setUp();
	}
	
	private void setUp(){
		blocks = new Block[size][size][size];
		generateGeneric(BlockType.BlockType_Stone);
	}

	
	/**
	 * Creates a chunk of only the given type
	 * @param type
	 */
	private void generateGeneric(BlockType type) {
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				for(int z = 0; z < size; z++){
					blocks[x][y][z] = new Block(type);
				}
			}
		}
	}
	
	
	
	
	
	
}
