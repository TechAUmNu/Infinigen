package newChunks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;

import oldgraphics.ChunkBatch;
import oldshaders.Shader;
import oldthreading.DataStore;
import newChunks.Block.BlockType;

import org.lwjgl.BufferUtils;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Chunk {

	/**
	 * The size of a chunk
	 */
	static final int CHUNK_SIZE = 16;

	/**
	 * The number of 'units' a block is on each axis
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
	private int VBONormalHandle;
	private int visibleFaces;
	int x;

	private int y;

	private int z;
	private String worldLocation;
	private Boolean test = false;
	private ChunkBatch batch;
	private newChunks.Block.BlockType type;
	Random rnd = new Random();
	private int VBOUVHandle;

	/**
	 * Updates this chunk
	 * 
	 * @param i
	 */
	public void Update() {
		// boolean changed = false;

		// Update the chunk
		// for (int x = 0; x < CHUNK_SIZE; x++) {
		// for (int y = 0; y < CHUNK_SIZE; y++) {
		// for (int z = 0; z < CHUNK_SIZE; z++) {
		// if(rnd.nextInt(100) > 95){
		// Blocks[x][y][z] = new Block(BlockType.BlockType_Air);
		// changed = true;
		// }
		// }
		// }
		// }

		// If something changed then
		// if(changed){
		RebuildChunk();
		// }
		//
	}

	/**
	 * Saves this chunk
	 * 
	 * @return If the save was successful
	 */
	public Boolean Save() {
		System.out.println("Saving Chunk: " + x + "." + y + "." + z);
		try (FileOutputStream file = new FileOutputStream(worldLocation + "" + x + "." + y + "." + z + ".chunk")) {
			byte[] buf = new byte[4096];
			int i = 0;

			// Add all blocks to a buffer
			for (int x = 0; x < CHUNK_SIZE; x++) {
				for (int y = 0; y < CHUNK_SIZE; y++) {
					for (int z = 0; z < CHUNK_SIZE; z++) {
						buf[i] = Blocks[x][y][z].GetType();
						i++;
					}
				}
			}

			LZMA2Options options = new LZMA2Options();
			XZOutputStream out = new XZOutputStream(file, options);
			out.write(buf);
			out.finish();
			out.close();
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
		// try (FileInputStream file = new FileInputStream(worldLocation + "" +
		// x
		// + "." + y + "." + z + ".chunk")) {
		// XZInputStream in = new XZInputStream(file);
		// byte fileContent[] = new byte[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
		// in.read(fileContent);
		// int i = 0;
		// for (int x = 0; x < CHUNK_SIZE; x++) {
		// for (int y = 0; y < CHUNK_SIZE; y++) {
		// for (int z = 0; z < CHUNK_SIZE; z++) {
		// Blocks[x][y][z] = new Block(
		// BlockType.fromByte(fileContent[i]));
		// i++;
		// }
		// }
		// }
		// file.close();
		// RebuildChunk();
		// return true;
		// } catch (FileNotFoundException e) {
		init();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return false;
	}

	/**
	 * Initiates the blocks in the chunk
	 */
	private void init() {
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					Blocks[x][y][z] = new Block(type);
				}
			}
		}
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
	public Chunk(int x, int y, int z, String worldLocation, BlockType type) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldLocation = worldLocation;
		VBOVertexHandle = glGenBuffers();
		VBOColorHandle = glGenBuffers();
		VBONormalHandle = glGenBuffers();
		VBOUVHandle = glGenBuffers();
		this.type = type;
	}

	public void CleanUp() {
		// Remove chunk from rendering queue
		DataStore.getInstance().removeChunkBatch(batch);
		// Delete buffers on GPU to free memory
		glDeleteBuffers(VBOVertexHandle);
		glDeleteBuffers(VBOColorHandle);
		glDeleteBuffers(VBONormalHandle);
		glDeleteBuffers(VBOUVHandle);

	}

	/**
	 * Checks the visibility of blocks, only blocks with air as a neighbour are
	 * visible
	 */
	public void RebuildChunk() {
		System.out.println("Updating Chunk: " + x + "." + y + "." + z);
		visibleFaces = 0;
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					visibleFaces += CheckFaces(x, y, z);
				}
			}
		}
		RebuildMesh();
	}

	/**
	 * Creates the visible mesh for the chunk
	 */
	public void RebuildMesh() {

		// Create FloatBuffers for all data
		FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(visibleFaces * 12);
		FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(visibleFaces * 12);
		FloatBuffer VertexNormalData = BufferUtils.createFloatBuffer(visibleFaces * 12);
		FloatBuffer VertexUVData = BufferUtils.createFloatBuffer(visibleFaces * 8);

		// Loop through all blocks in chunk adding visible faces to buffers
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					if (Blocks[(int) x][(int) y][(int) z].IsVisible()) {

						// Add position data to buffer
						VertexPositionData.put(Blocks[x][y][z].getVf().genVertexes(this.x * MULTIPLIER + x * CUBE_LENGTH,
								this.y * MULTIPLIER + y * CUBE_LENGTH, this.z * MULTIPLIER + z * CUBE_LENGTH, CUBE_LENGTH));

						// Add colour data to buffer
						VertexColorData.put(Blocks[x][y][z].getVf().genColors());

						// Add normal data to buffer
						VertexNormalData.put(Blocks[x][y][z].getVf().genNormals());

						// Add UV coordinates to buffer
						VertexUVData.put(Blocks[x][y][z].getVf().genUV());

					}
				}
			}

		}

		VertexColorData.flip();
		VertexPositionData.flip();
		VertexNormalData.flip();
		VertexUVData.flip();
		glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, VBONormalHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexNormalData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, VBOUVHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexUVData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		if (batch != null) {
			DataStore.getInstance().removeChunkBatch(batch);
		}
		ChunkBatch b = new ChunkBatch(Shader.Chunk);
		b.addVBO(VBOVertexHandle, VBOColorHandle, VBONormalHandle, visibleFaces, VBOUVHandle);
		batch = b;
		DataStore.getInstance().addChunkBatch(b);

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
	public int CheckFaces(int x, int y, int z) {
		if (Blocks[x][y][z].GetType() == BlockType.BlockType_Air.GetType()) {
			return 0;
		}
		VisibleFaces vf = new VisibleFaces();
		Blocks[x][y][z].SetVisible(false);
		int visible = 0;
		if (y < 15) {
			if (Blocks[x][y + 1][z].GetType() == BlockType.BlockType_Air.GetType()) {
				vf.top = true;
				Blocks[x][y][z].SetVisible(true);
				visible++;
			} else {
				vf.top = false;

			}
		} else {
			vf.top = true;
			Blocks[x][y][z].SetVisible(true);
			visible++;
		}
		if (y > 0) {
			if (Blocks[x][y - 1][z].GetType() == BlockType.BlockType_Air.GetType()) {
				vf.bottom = true;
				Blocks[x][y][z].SetVisible(true);
				visible++;
			} else {
				vf.bottom = false;

			}
		} else {
			vf.bottom = true;
			Blocks[x][y][z].SetVisible(true);
			visible++;
		}
		if (x < 15) {
			if (Blocks[x + 1][y][z].GetType() == BlockType.BlockType_Air.GetType()) {
				vf.right = true;
				Blocks[x][y][z].SetVisible(true);
				visible++;
			} else {
				vf.right = false;

			}
		} else {
			vf.right = true;
			Blocks[x][y][z].SetVisible(true);
			visible++;
		}

		if (x > 0) {
			if (Blocks[x - 1][y][z].GetType() == BlockType.BlockType_Air.GetType()) {
				vf.left = true;
				Blocks[x][y][z].SetVisible(true);
				visible++;
			} else {
				vf.left = false;

			}
		} else {
			vf.left = true;
			Blocks[x][y][z].SetVisible(true);
			visible++;
		}
		if (z < 15) {
			if (Blocks[x][y][z + 1].GetType() == BlockType.BlockType_Air.GetType()) {
				vf.front = true;
				Blocks[x][y][z].SetVisible(true);
				visible++;
			} else {
				vf.front = false;

			}
		} else {
			vf.front = true;
			Blocks[x][y][z].SetVisible(true);
			visible++;
		}
		if (z > 0) {
			if (Blocks[x][y][z - 1].GetType() == BlockType.BlockType_Air.GetType()) {
				vf.back = true;
				Blocks[x][y][z].SetVisible(true);
				visible++;
			} else {
				vf.back = false;

			}
		} else {
			vf.back = true;
			Blocks[x][y][z].SetVisible(true);
			visible++;
		}
		Blocks[x][y][z].setVf(vf);
		return visible;
	}

}
