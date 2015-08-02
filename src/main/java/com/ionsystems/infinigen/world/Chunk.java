package main.java.com.ionsystems.infinigen.world;

import com.sudoplay.joise.module.Module;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.physics.PhysicsProcessor;
import main.java.com.ionsystems.infinigen.world.BlockType;

/**
 * A chunk
 * 
 * @author Euan
 *
 */
public class Chunk {

	public Block[][][] blocks;
	public int x;
	public int y;
	public int z;
	public int size;
	public float blockSize;
	public boolean visible;
	public boolean changed = false;
	Module terrainNoise;

	PhysicsModel bottomModel, topModel, frontModel, backModel, leftModel, rightModel;

	public Chunk(int x, int y, int z, int size, float blockSize, Module terrainNoise) {

		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.blockSize = blockSize;
		this.terrainNoise = terrainNoise;
		//if (Globals.isServer()) {
			
		setUp();
		//}
		//System.out.println("TEST");
		if (!Globals.isServer()) {
			rebuild();
		}

	}
	
	public Chunk(ChunkData cd){
		this.x = cd.x;
		this.y = cd.y;
		this.z = cd.z;
		this.size = cd.size;
		this.blockSize = cd.blockSize;
		this.blocks = cd.blocks;
		
		//System.out.println("TEST");
		if (!Globals.isServer()) {
			rebuild(); 
		}
	}

	private void setUp() {
		blocks = new Block[size][size][size];
		generateGeneric(BlockType.BlockType_Air);
		generateType(BlockType.BlockType_Grass);
	}

	private void generateType(BlockType type) {	
		
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				
				
				float xWorld = x + (size * this.x);
				float zWorld = z + (size * this.z);
				//System.out.println(xWorld / 300);
				
				double height = terrainNoise.get(xWorld / 300f, zWorld / 300f);	
				
				//System.out.println(height);
				for(int y = 0; y < height; y++){
					blocks[x][y][z] = new Block(type);
				}
			}			
		}

		
		
	}

	/**
	 * Creates a chunk of only the given type
	 * 
	 * @param type
	 */
	private void generateGeneric(BlockType type) {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
					blocks[x][y][z] = new Block(type);
				}
			}
		}
	}

	public void update() {
		// Check if chunk has changed, if it has then we need to change what we
		// render#
		if (changed) {
			if (!Globals.isServer()) { //We don't do this on server since we don't draw anything
				rebuild();
			}
		}
	}

	private void rebuild() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
					CheckFaces(x, y, z);
				}
			}
		}
		rebuildRendering();
	}

	private void rebuildRendering() {

		RenderingFace front = new RenderingFace();
		RenderingFace back = new RenderingFace();
		RenderingFace top = new RenderingFace();
		RenderingFace bottom = new RenderingFace();
		RenderingFace left = new RenderingFace();
		RenderingFace right = new RenderingFace();

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
					if (blocks[x][y][z].isVisible()) {
						Block b = blocks[x][y][z];
						if (b.isFrontVisible()) {
							front.addFace(x, y, z, blockSize, Face.Front);
						}
						if (b.isBackVisible()) {
							back.addFace(x, y, z, blockSize, Face.Back);
						}
						if (b.isTopVisible()) {
							top.addFace(x, y, z, blockSize, Face.Top);
						}
						if (b.isBottomVisible()) {
							bottom.addFace(x, y, z, blockSize, Face.Bottom);
						}
						if (b.isLeftVisible()) {
							left.addFace(x, y, z, blockSize, Face.Left);
						}
						if (b.isRightVisible()) {
							right.addFace(x, y, z, blockSize, Face.Right);
						}

					}
				}
			}
		}
		
//		PhysicsProcessor processor = Globals.getPhysics().getProcessor();
//		
//		if(frontModel != null){
//			processor.removeRigidBody(frontModel.getBody());
//			processor.removeRigidBody(backModel.getBody());
//			processor.removeRigidBody(topModel.getBody());
//			processor.removeRigidBody(bottomModel.getBody());
//			processor.removeRigidBody(leftModel.getBody());
//			processor.removeRigidBody(rightModel.getBody());
//		}

						
		frontModel = front.getModel();
		backModel = back.getModel();
		topModel = top.getModel();
		bottomModel = bottom.getModel();
		leftModel = left.getModel();
		rightModel = right.getModel();
		
//		processor.addRigidBody(frontModel.getBody());
//		processor.addRigidBody(backModel.getBody());
//		processor.addRigidBody(topModel.getBody());
//		processor.addRigidBody(bottomModel.getBody());
//		processor.addRigidBody(leftModel.getBody());
//		processor.addRigidBody(rightModel.getBody());
		

	}

	public RawModel getBottomModel() {
		return bottomModel;
	}

	public RawModel getTopModel() {
		return topModel;
	}

	public RawModel getFrontModel() {
		return frontModel;
	}

	public RawModel getBackModel() {
		return backModel;
	}

	public RawModel getLeftModel() {
		return leftModel;
	}

	public RawModel getRightModel() {
		return rightModel;
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
	public void CheckFaces(int x, int y, int z) {
		if (blocks[x][y][z].GetType() == BlockType.BlockType_Air.GetType()) {
			return;
		}

		blocks[x][y][z].SetVisible(false);

		if (y < size - 1) {
			if (blocks[x][y + 1][z].GetType() == BlockType.BlockType_Air.GetType()) {
				blocks[x][y][z].setTop(true);
				blocks[x][y][z].SetVisible(true);

			} else {
				blocks[x][y][z].setTop(false);

			}
		} else {
			blocks[x][y][z].setTop(true);
			blocks[x][y][z].SetVisible(true);

		}
		if (y > 0) {
			if (blocks[x][y - 1][z].GetType() == BlockType.BlockType_Air.GetType()) {
				blocks[x][y][z].setBottom(true);
				blocks[x][y][z].SetVisible(true);

			} else {
				blocks[x][y][z].setBottom(false);

			}
		} else {
			blocks[x][y][z].setBottom(true);
			blocks[x][y][z].SetVisible(true);

		}
		if (x < size - 1) {
			if (blocks[x + 1][y][z].GetType() == BlockType.BlockType_Air.GetType()) {
				blocks[x][y][z].setRight(true);
				blocks[x][y][z].SetVisible(true);

			} else {
				blocks[x][y][z].setRight(false);

			}
		} else {
			blocks[x][y][z].setRight(true);
			blocks[x][y][z].SetVisible(true);

		}

		if (x > 0) {
			if (blocks[x - 1][y][z].GetType() == BlockType.BlockType_Air.GetType()) {
				blocks[x][y][z].setLeft(true);
				blocks[x][y][z].SetVisible(true);

			} else {
				blocks[x][y][z].setLeft(false);

			}
		} else {
			blocks[x][y][z].setLeft(true);
			blocks[x][y][z].SetVisible(true);

		}
		if (z < size - 1) {
			if (blocks[x][y][z + 1].GetType() == BlockType.BlockType_Air.GetType()) {
				blocks[x][y][z].setFront(true);
				blocks[x][y][z].SetVisible(true);

			} else {
				blocks[x][y][z].setFront(false);

			}
		} else {
			blocks[x][y][z].setFront(true);
			blocks[x][y][z].SetVisible(true);

		}
		if (z > 0) {
			if (blocks[x][y][z - 1].GetType() == BlockType.BlockType_Air.GetType()) {
				blocks[x][y][z].setBack(true);
				blocks[x][y][z].SetVisible(true);

			} else {
				blocks[x][y][z].setBack(false);

			}
		} else {
			blocks[x][y][z].setBack(true);
			blocks[x][y][z].SetVisible(true);

		}

	}

	public ChunkData getData() {
		ChunkData cd = new ChunkData();
		cd.blocks = blocks;
		cd.x = x;
		cd.y = y;
		cd.z = z;
		cd.blockSize = blockSize;
		cd.size = size;
		return cd;
	}

	public void cleanUp() {
		bottomModel.cleanUp();
		topModel.cleanUp();
		frontModel.cleanUp();
		backModel.cleanUp();
		leftModel.cleanUp();
		rightModel.cleanUp();	
		blocks = null;
	}

}
