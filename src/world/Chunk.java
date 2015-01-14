package world;

import graphics.ChunkBatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import threading.InterthreadHolder;
import world.Block.BlockType;

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
	private int x, y, z;
	private String worldLocation;
	private Boolean test = false;
	private ChunkBatch batch;
	private BlockType type;

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
			byte[] buf = new byte[4096];
			int i = 0;
			
			//Add all blocks to a buffer
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
		this.type = type;
	}

	public void CleanUp() {
		glDeleteBuffers(VBOVertexHandle);
		glDeleteBuffers(VBOColorHandle);
		glDeleteBuffers(VBONormalHandle);
	}

	/**
	 * Checks the visibility of blocks, only blocks with air as a neighbour are
	 * visible
	 */
	public void RebuildChunk() {
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
		System.out.println(visibleFaces);
		FloatBuffer VertexPositionData = BufferUtils
				.createFloatBuffer(visibleFaces * 12);
		FloatBuffer VertexColorData = BufferUtils
				.createFloatBuffer(visibleFaces * 12);
		FloatBuffer VertexNormalData = BufferUtils
				.createFloatBuffer(visibleFaces * 12);
		int i = 0;
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				for (int z = 0; z < CHUNK_SIZE; z++) {
					if (Blocks[(int) x][(int) y][(int) z].IsVisible()) {
						VertexPositionData.put(Blocks[x][y][z].getVf()
								.genVertexes(
										this.x * MULTIPLIER + x * CUBE_LENGTH,
										this.y * MULTIPLIER + y * CUBE_LENGTH,
										this.z * MULTIPLIER + z * CUBE_LENGTH,
										CUBE_LENGTH));
						VertexColorData
								.put(Blocks[x][y][z].getVf().genColors());
						VertexNormalData.put(Blocks[x][y][z].getVf()
								.genNormals());
						System.out.println(i);
						i++;
					}
				}
			}

		}

		
		VertexColorData.flip();
		VertexPositionData.flip();
		VertexNormalData.flip();
		glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, VBONormalHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexNormalData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		//System.out.println(visibleFaces);
		if (batch != null) {
			InterthreadHolder.getInstance().removeChunkBatch(batch);
		}
		ChunkBatch b = new ChunkBatch("shaders/landscape.vs",
				"shaders/landscape.fs");
		b.addVBO(VBOVertexHandle, VBOColorHandle, VBONormalHandle, visibleFaces);
		batch = b;
		InterthreadHolder.getInstance().addChunkBatch(b);
		
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
			if (Blocks[x][y + 1][z].GetType() == BlockType.BlockType_Air
					.GetType()) {
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
			if (Blocks[x][y - 1][z].GetType() == BlockType.BlockType_Air
					.GetType()) {
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
			if (Blocks[x + 1][y][z].GetType() == BlockType.BlockType_Air
					.GetType()) {
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
			if (Blocks[x - 1][y][z].GetType() == BlockType.BlockType_Air
					.GetType()) {
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
			if (Blocks[x][y][z + 1].GetType() == BlockType.BlockType_Air
					.GetType()) {
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
			if (Blocks[x][y][z - 1].GetType() == BlockType.BlockType_Air
					.GetType()) {
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
