package world;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import world.Block.BlockType;

public class Chunk {

	static final int CHUNK_SIZE = 16;
	static final int CUBE_LENGTH = 2;
	private Block[][][] Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
	private int VBOVertexHandle;
	private int VBOColorHandle;
	private int x, y, z;
	private String worldLocation;

	public void Render() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOVertexHandle);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0L);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOColorHandle);
		GL11.glColorPointer(3, GL11.GL_FLOAT, 0, 0L);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE
				* CHUNK_SIZE * 24);
	}

	public void Update() {
		// Update the chunk
	}

	public Boolean Save() {
		try(FileOutputStream file = new FileOutputStream(worldLocation +"\\" + x+y+z)){
			for(int x = 0; x < CHUNK_SIZE; x++){
				for(int y = 0; y < CHUNK_SIZE; y++){
					for(int z = 0; z < CHUNK_SIZE; z++){
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
	
	public Boolean Load() {		
		try(FileInputStream file = new FileInputStream(worldLocation +"\\" + x+y+z)){
			byte fileContent[] = new byte[CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE];
			file.read(fileContent);
			int i = 0;
			for(int x = 0; x < CHUNK_SIZE; x++){
				for(int y = 0; y < CHUNK_SIZE; y++){
					for(int z = 0; z < CHUNK_SIZE; z++){
						//Blocks[x][y][z] = new Block(BlockType.fromByte(fileContent[i]));
						//
						if(Blocks[x][y][z].GetType() == BlockType.BlockType_Air.GetType()) {
							System.out.println(Blocks[x][y][z].GetType());
							System.out.println("Found");
							System.out.println(i);
							System.out.println(" " + x  +" " + y + " " +z);
						}
					
						i++;
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
	
	public static void main(String[] args){
		Chunk c = new Chunk(0,0,0, "C:\\eclipse\\workspace\\Infinigen\\test");
		c.init(true);
		c.Save();
		Chunk s = new Chunk(0,0,0,"C:\\eclipse\\workspace\\Infinigen\\test");
		s.init(false);
		s.Load();
	}

	//Initiate the chunk to all dirt
	private void init(Boolean check) {
		for(int x = 0; x < CHUNK_SIZE; x++){
			for(int y = 0; y < CHUNK_SIZE; y++){
				for(int z = 0; z < CHUNK_SIZE; z++){
					Blocks[x][y][z] = new Block(BlockType.BlockType_Dirt);
				}
			}
		}
		if(check){
		Blocks[5][5][5].setType(BlockType.BlockType_Air);
		}
	}

	

	public Chunk(int x, int y, int z, String worldLocation) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldLocation = worldLocation;
	}

	// This method makes sure we only draw visible blocks
	public void RebuildChunk(){
		for(int x = 0; x < CHUNK_SIZE; x++){
			for(int y = 0; y < CHUNK_SIZE; y++){
				for(int z = 0; z < CHUNK_SIZE; z++){
					if(NeighboorAir(x,y,z)){
						Blocks[x][y][z].SetVisible(true);
					}else{
						Blocks[x][y][z].SetVisible(false);
					}					
				}
			}
		}
		RebuildMesh();
	}

	// This method creates the visible mesh
	public void RebuildMesh() {
		// Rebuild the view mesh of the chunk
		FloatBuffer VertexPositionData = BufferUtils
				.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
		FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE
				* CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

		for (float x = 0; x < CHUNK_SIZE; x++) {
			for (float y = 0; y < CHUNK_SIZE; y++) {
				for (float z = 0; z < CHUNK_SIZE; z++) {
					if (Blocks[(int) x][(int) y][(int) z].IsVisible()) {

						VertexPositionData.put(CreateCube((float) this.x + x
								* CUBE_LENGTH,
								(float) this.y + y * CUBE_LENGTH,
								(float) this.z + z * CUBE_LENGTH));
						VertexColorData
								.put(CreateCubeVertexCol(GetCubeColor(Blocks[(int) x][(int) y][(int) z])));

					}
				}
			}

		}

		VertexColorData.flip();
		VertexPositionData.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOVertexHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VertexPositionData,
				GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOColorHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VertexColorData,
				GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

	}

	private float[] CreateCubeVertexCol(float[] CubeColorArray) {
		float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
		for (int i = 0; i < cubeColors.length; i++) {
			cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
		}
		return cubeColors;
	}

	// This method checks if any blocks next to the position are air blocks.
	public Boolean NeighboorAir(int x, int y, int z) {
		// We put y+1 first since it is the most likely block to have air above
		// it
		if (Blocks[x][y + 1][z].GetType() == BlockType.BlockType_Air.GetType())
			return true;
		if (Blocks[x][y - 1][z].GetType() == BlockType.BlockType_Air.GetType())
			return true;
		if (Blocks[x + 1][y][z].GetType() == BlockType.BlockType_Air.GetType())
			return true;
		if (Blocks[x - 1][y][z].GetType() == BlockType.BlockType_Air.GetType())
			return true;
		if (Blocks[x][y][z + 1].GetType() == BlockType.BlockType_Air.GetType())
			return true;
		if (Blocks[x][y][z - 1].GetType() == BlockType.BlockType_Air.GetType())
			return true;
		return false;

	}

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
	/*
	 * private float[] GetNormalVector() { return new float[] { // BOTTOM 0, 1,
	 * 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // TOP 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
	 * 0, // FRONT 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // BOTTOM 0, 0, 1, 0, 0,
	 * 1, 0, 0, 1, 0, 0, 1, // LEFT QUAD 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, //
	 * RIGHT QUAD -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, }; }
	 */
}
