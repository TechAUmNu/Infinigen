package newWorld;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import newModels.RawModel;
import newWorld.BlockType;

/**
 * A chunk
 * 
 * @author Euan
 *
 */
public class Chunk {

	Block[][][] blocks;
	int x, y, z, size;
	float blockSize;
	boolean visible;
	boolean changed = false;

	
	RawModel bottomModel, topModel, frontModel, backModel, leftModel, rightModel;

	public Chunk(int x, int y, int z, int size, float blockSize) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.blockSize = blockSize;
		setUp();
		rebuild();
		
	}

	private void setUp() {
		blocks = new Block[size][size][size];
		generateGeneric(BlockType.BlockType_Stone);
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
			rebuild();
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
		
		frontModel = front.getModel();
		backModel = back.getModel();
		topModel = top.getModel();
		bottomModel = bottom.getModel();
		leftModel = left.getModel();
		rightModel = right.getModel();
		
		
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
		if (x <  size - 1) {
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
		if (z <  size - 1) {
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

}
