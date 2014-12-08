package world;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import world.Block.BlockType;

public class Chunk {

	/**
	 * The size of a chunk
	 */
	static final int CHUNK_SIZE = 16;

	/**
	 * The number of 'pixels' a block is on each axis
	 */
	static final int CUBE_LENGTH = 2;

	/**
	 * The multiplier to find the world position
	 */
	static final int MULTIPLIER = CHUNK_SIZE * CUBE_LENGTH;
	/**
	 * 3d array to hold blocks
	 */
	private Block[][][] Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	private int VBOVertexHandle;
	private int VBOColorHandle;
	private int visibleBlocks;
	private int x, y, z;
	private String worldLocation;
	private Boolean test = false;

	/**
	 * Renders this chunk
	 */
	public void Render() {
		//System.out.println(visibleBlocks);
		if(visibleBlocks > 0){ //No point in rendering nothing!
			glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
			glVertexPointer(3, GL_FLOAT, 0, 0L);
			glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
			glColorPointer(3, GL_FLOAT, 0, 0L);
			glDrawArrays(GL_QUADS, 0, visibleBlocks * 24);
		}
	}

	/**
	 * Updates this chunk
	 */
	public void Update() {
		// Update the chunk
		// If something changed then
		// RebuildChunk();
	}

	/**
	 * Saves this chunk
	 * 
	 * @return If the save was successful
	 */
	public Boolean Save() {
		try (FileOutputStream file = new FileOutputStream(worldLocation + "\\"
				+ x + y + z + ".chunk")) {
			for (int x = 0; x < CHUNK_SIZE; x++) {
				for (int y = 0; y < CHUNK_SIZE; y++) {
					for (int z = 0; z < CHUNK_SIZE; z++) {
						file.write(Blocks[x][y][z].GetType());
					}
				}
			}
			file.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Loads this chunk
	 * 
	 * @return If the load was successful
	 */
	public Boolean Load() {
		try (FileInputStream file = new FileInputStream(worldLocation + "\\"
				+ x + y + z + ".chunk")) {
			byte fileContent[] = new byte[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
			file.read(fileContent);
			int i = 0;
			for (int x = 0; x < CHUNK_SIZE; x++) {
				for (int y = 0; y < CHUNK_SIZE; y++) {
					for (int z = 0; z < CHUNK_SIZE; z++) {
						Blocks[x][y][z] = new Block(
								BlockType.fromByte(fileContent[i]));
						i++;
					}
				}
			}
			file.close();
			RebuildChunk();
			return true;
		} catch (FileNotFoundException e) {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Initiates the blocks in the chunk
	 */
	private void init() {
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					Blocks[x][y][z] = new Block(BlockType.BlockType_Dirt);
				}
			}
		}
		Blocks[0][0][0].setType(BlockType.BlockType_Air);
		Blocks[3][3][3].setType(BlockType.BlockType_Air);
		
		System.out.println(Blocks[3][3][3].GetType());
		System.out.println(Blocks[0][0][0].GetType());
		System.out.println(Blocks[3][2][3].GetType());
		
		RebuildChunk();
		
	}

	/**
	 * Creates a new chunk
	 * 
	 * @param x
	 *            X position of the chunk
	 * @param y
	 *            Y position of the chunk
	 * @param z
	 *            Z position of the chunk
	 * @param worldLocation
	 *            Location of the world on disk
	 */
	public Chunk(int x, int y, int z, String worldLocation) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldLocation = worldLocation;
		VBOVertexHandle = glGenBuffers();
		VBOColorHandle = glGenBuffers();
	}
	
	public void CleanUp(){
		glDeleteBuffers(VBOVertexHandle);
		glDeleteBuffers(VBOColorHandle);
	}

	/**
	 * Checks the visibility of blocks, only blocks with air as a neighbour are
	 * visible
	 */
	public void RebuildChunk() {
		visibleBlocks = 0;
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					if (NeighbourAir(x, y, z)) {
						Blocks[x][y][z].SetVisible(true);
						visibleBlocks++;
					} else {
						Blocks[x][y][z].SetVisible(false);
					}
					//System.out.println(Blocks[x][y][z].GetType());
				}
			}
		}
		RebuildMesh();
	}

	/**
	 * Creates the visible mesh for the chunk
	 */
	public void RebuildMesh() {
		// Rebuild the view mesh of the chunk
		FloatBuffer VertexPositionData = BufferUtils
				.createFloatBuffer(visibleBlocks * 6 * 12);
		FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(visibleBlocks * 6 * 12);

		for (float x = 0; x < CHUNK_SIZE; x++) {
			for (float y = 0; y < CHUNK_SIZE; y++) {
				for (float z = 0; z < CHUNK_SIZE; z++) {
					if (Blocks[(int) x][(int) y][(int) z].IsVisible()) {

						VertexPositionData.put(CreateCube((float) this.x
								* MULTIPLIER + x * CUBE_LENGTH, (float) this.y
								* MULTIPLIER + y * CUBE_LENGTH, (float) this.z
								* MULTIPLIER + z * CUBE_LENGTH));
						VertexColorData
								.put(CreateCubeVertexCol(GetCubeColor(Blocks[(int) x][(int) y][(int) z])));

					}
				}
			}

		}

		VertexColorData.flip();
		VertexPositionData.flip();
		glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

	}

	private float[] CreateCubeVertexCol(float[] CubeColorArray) {
		float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
		for (int i = 0; i < cubeColors.length; i++) {
			cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
		}
		return cubeColors;
	}

	/**
	 * Checks if any neighbour blocks are air
	 * 
	 * @param x
	 *            X position of the block in the chunk
	 * @param y
	 *            Y position of the block in the chunk
	 * @param z
	 *            Z position of the block in the chunk
	 * @return If any neighbours are air
	 */
	public Boolean NeighbourAir(int x, int y, int z) {
		// We put y+1 first since it is the most likely block to have air above
		// it
		if (y < 15)
			if (Blocks[x][y + 1][z].GetType() == BlockType.BlockType_Air
					.GetType())
				return true;
		if (y > 0)
			if (Blocks[x][y - 1][z].GetType() == BlockType.BlockType_Air
					.GetType())
				return true;
		if (x < 15)
			if (Blocks[x + 1][y][z].GetType() == BlockType.BlockType_Air
					.GetType())
				return true;
		if (x > 0)
			if (Blocks[x - 1][y][z].GetType() == BlockType.BlockType_Air
					.GetType())
				return true;
		if (z < 15)
			if (Blocks[x][y][z + 1].GetType() == BlockType.BlockType_Air
					.GetType())
				return true;
		if (z > 0)
			if (Blocks[x][y][z - 1].GetType() == BlockType.BlockType_Air
					.GetType())
				return true;
		return false;

	}

	/**
	 * Creates a float array defining a block.
	 * 
	 * @param x
	 *            X world position of the block
	 * @param y
	 *            Y world position of the block
	 * @param z
	 *            Z world position of the block
	 * @return The float array defining the block
	 */
	public static float[] CreateCube(float x, float y, float z) {
		int offset = CUBE_LENGTH / 2;
		return new float[] {
				// BOTTOM QUAD(DOWN=+Y)
				x + offset, y + offset,
				z,
				x - offset,
				y + offset,
				z,
				x - offset,
				y + offset,
				z - CUBE_LENGTH,
				x + offset,
				y + offset,
				z - CUBE_LENGTH,
				// TOP!
				x + offset, y - offset, z - CUBE_LENGTH, x - offset,
				y - offset,
				z - CUBE_LENGTH,
				x - offset,
				y - offset,
				z,
				x + offset,
				y - offset,
				z,
				// FRONT QUAD
				x + offset, y + offset, z - CUBE_LENGTH, x - offset,
				y + offset, z - CUBE_LENGTH, x - offset,
				y - offset,
				z - CUBE_LENGTH,
				x + offset,
				y - offset,
				z - CUBE_LENGTH,
				// BACK QUAD
				x + offset, y - offset, z, x - offset, y - offset, z,
				x - offset, y + offset, z,
				x + offset,
				y + offset,
				z,
				// LEFT QUAD
				x - offset, y + offset, z - CUBE_LENGTH, x - offset,
				y + offset, z, x - offset, y - offset, z, x - offset,
				y - offset,
				z - CUBE_LENGTH,
				// RIGHT QUAD
				x + offset, y + offset, z, x + offset, y + offset,
				z - CUBE_LENGTH, x + offset, y - offset, z - CUBE_LENGTH,
				x + offset, y - offset, z };

	}

	private float[] GetCubeColor(Block block) {
		switch (block.GetType()) {
		case 1:
			return new float[] { 0, 1, 0 };
		case 2:
			return new float[] { 1, 0.5f, 0 };
		}
		return new float[] { 1, 1, 1 };
	}

	private float[] GetNormalVector() {
		return new float[] {
				// BOTTOM
				0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
				// TOP
				0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
				// FRONT
				0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
				// BOTTOM
				0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
				// LEFT QUAD
				1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
				// RIGHT QUAD
				-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0 };
	}

}
