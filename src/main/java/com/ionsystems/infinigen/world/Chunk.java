package main.java.com.ionsystems.infinigen.world;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.xxhash.XXHashFactory;

import org.lwjgl.util.vector.Vector3f;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import com.sudoplay.joise.module.Module;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.world.BlockType;

/**
 * A chunk
 * 
 * @author Euan
 *
 */
public class Chunk {
	public ChunkID chunkID;
	private static float isolevel = 10f;

	private boolean renderable = false;

	private HashMap<VertexID, Vertex> verts = new HashMap<VertexID, Vertex>();
	private ArrayList<Integer> indicies = new ArrayList<Integer>();
	private ArrayList<VertexID> orderOfVerts = new ArrayList<VertexID>();
	private ArrayList<Triangle> triangles = new ArrayList<Triangle>();
	public Block[][][] blocks;
	public int x;
	public int y;
	public int z;
	public int size;
	public int sizey;
	public float blockSize;
	public boolean visible;
	public boolean changed = false;
	Module terrainNoise;
	private byte[] uncompressedData;

	PhysicsModel model;
	private int vertID;

	private NetworkChunkData ncd;
	private ChunkRenderingData crd;

	final LZ4Compressor compressor = LZ4Factory.nativeInstance().fastCompressor();
	final LZ4FastDecompressor decompressor = LZ4Factory.nativeInstance().fastDecompressor();

	/**
	 * Only called on the server to generate a chunk
	 * 
	 * @param chunkID
	 * @param x
	 * @param y
	 * @param z
	 * @param size
	 * @param blockSize
	 * @param terrainNoise
	 */
	public Chunk(ChunkID chunkID, int x, int y, int z, int size, int sizey, float blockSize, Module terrainNoise) {
		this.chunkID = chunkID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size + 1;
		this.sizey = sizey + 1;
		this.blockSize = blockSize;
		this.terrainNoise = terrainNoise;		
		if (Globals.isServer()) {
			setUp();
		}
	}

	/**
	 * Only called on the client to render a chunk
	 * 
	 * @param ncd
	 */
	public Chunk(NetworkChunkData ncd) {		
		this.x = ncd.x;
		this.y = ncd.y;
		this.z = ncd.z;
		this.size = ncd.size;
		this.sizey = ncd.sizey;
		this.blockSize = ncd.blockSize;		
		this.uncompressedData = ncd.uncompressedData;
		this.chunkID = ncd.chunkID;		
		load(this.uncompressedData);
		// Now we have processed the chunk data, we dont actually need to keep it in memory.
		// We can safely unload it untill we need to reload this chunk from disk
		this.uncompressedData = null;
		
	}

	private void setUp() {

		if (!loadToDataFile()) {
			generateType(BlockType.BlockType_Grass);
		}
		setNcd(new NetworkChunkData(uncompressedData, x, y, z, blockSize, size, sizey, chunkID));
	}

	/**
	 * Saves this chunk
	 * 
	 * @return If the save was successful
	 */
	public Boolean save() {
		System.out.println("Saving Chunk: " + x + "." + y + "." + z);
		// System.out.println("Saving Chunk: " + x + "." + y + "." + z);
		try (FileOutputStream file = new FileOutputStream("world/" + x + "." + y + "." + z + ".chunk")) {
			byte[] buf = new byte[size * sizey * size * 5];
			int i = 0;

			// Add all blocks to a buffer
			int bits;
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < sizey; y++) {
					for (int z = 0; z < size; z++) {
						buf[i++] = blocks[x][y][z].GetType();
						bits = Float.floatToIntBits(blocks[x][y][z].weight);
						buf[i++] = (byte) (bits & 0xff);
						buf[i++] = (byte) ((bits >> 8) & 0xff);
						buf[i++] = (byte) ((bits >> 16) & 0xff);
						buf[i++] = (byte) ((bits >> 24) & 0xff);

					}
				}
			}
			uncompressedData = buf;
			LZ4BlockOutputStream out = new LZ4BlockOutputStream(file, 32 * 1024 * 1024, compressor, XXHashFactory.fastestInstance().newStreamingHash32(128313).asChecksum(), true);
			out.write(buf);
			out.finish();
			out.close();
			file.close();
			blocks = null;
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

	private boolean save(byte[] uncompressedData) {
		System.out.println("Saving Chunk: " + x + "." + y + "." + z);
		try (FileOutputStream file = new FileOutputStream("world/" + x + "." + y + "." + z + ".chunk")) {
			LZ4BlockOutputStream out = new LZ4BlockOutputStream(file, 32 * 1024 * 1024, compressor, XXHashFactory.fastestInstance().newStreamingHash32(128313).asChecksum(), true);
			out.write(uncompressedData);
			out.finish();
			out.close();
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
	public Boolean loadToDataFile() {
		System.out.println("Loading Chunk: " + x + "." + y + "." + z);
		

		try (FileInputStream file = new FileInputStream("world/" + x + "." + y + "." + z + ".chunk")) {
			//Block[][][] tempBlocks = new Block[size][size][size];
			LZ4BlockInputStream in = new LZ4BlockInputStream(file, decompressor, XXHashFactory.fastestInstance().newStreamingHash32(128313).asChecksum());
			byte fileContent[] = new byte[size * sizey * size * 5];
			in.read(fileContent);
//			int i = 0;
//			for (int x = 0; x < size; x++) {
//				for (int y = 0; y < size; y++) {
//					for (int z = 0; z < size; z++) {
//						tempBlocks[x][y][z] = new Block(BlockType.fromByte(fileContent[i++]));
//						byte[] bytes = { fileContent[i++], fileContent[i++], fileContent[i++], fileContent[i++] };
//						tempBlocks[x][y][z].weight = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//						bytes = null;
//					}
//				}
//			}

			in.close();
			file.close();
			if (Globals.isServer()) {
				//blocks = tempBlocks;
				//tempBlocks = null;
				uncompressedData = fileContent;
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			System.out.println("FAILED: Loading Chunk: " + x + "." + y + "." + z );
			System.out.println("Reason: IOException");
			
			return false;
		}
		
	}

	/**
	 * Will only be called by a client when receiving a chunk from the server
	 * 
	 * @param uncompressedData
	 * @return
	 */
	public void load(byte[] uncompressedData) {
		System.out.println("Loading Chunk: " + x + "." + y + "." + z);
		blocks = new Block[size][sizey][size];

		int i = 0;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < sizey; y++) {
				for (int z = 0; z < size; z++) {
					blocks[x][y][z] = new Block(BlockType.fromByte(uncompressedData[i++]));
					byte[] bytes = { uncompressedData[i++], uncompressedData[i++], uncompressedData[i++], uncompressedData[i++] };
					blocks[x][y][z].weight = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
					bytes = null;
				}
			}
		}

		marchingCubes();
		save(uncompressedData); // Commented out to stop server and client overwriting each other.
		

		
	}

	
	public void cleanUp() {
		if (model != null) {
			Globals.getUnloadingLock().writeLock().lock();
			Globals.getLoader().addModelToUnloadQueue(model);
			Globals.getUnloadingLock().writeLock().unlock();
		}

		blocks = null;
	}

	private void generateType(BlockType type) {
		blocks = new Block[this.size][this.sizey][this.size];
		float xWorld;
		float zWorld;
		double height;
		int terrainChunkHeight = 1;
	
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {

				xWorld = x + (ChunkManager.chunkSize * this.x);
				zWorld = z + (ChunkManager.chunkSize * this.z);
				// System.out.println(xWorld / 300);
				if(this.y <= -(terrainChunkHeight+1)){
					for (int y = 0; y < sizey; y++) {
						blocks[x][y][z] = new Block(type);
						blocks[x][y][z].weight = y;
					}					
				}else if(this.y >=-terrainChunkHeight && this.y < 0){
					int chunkHeight = this.y + terrainChunkHeight; //1-8
					height = (sizey * terrainChunkHeight) - terrainNoise.get(xWorld/100, 0.5, zWorld/100);
					// height = 5;
					float weight;
					for (int y = 0; y < sizey; y++) {
						weight = 0;
						if (y + (chunkHeight * sizey)  < height) {
							weight = (float) (height / 10f);
							blocks[x][y][z] = new Block(type);
							blocks[x][y][z].weight = weight;
						}else{
							blocks[x][y][z] = new Block(BlockType.BlockType_Air);
						}
						
					}
				}else{
					for (int y = 0; y < sizey; y++) {
						blocks[x][y][z] = new Block(BlockType.BlockType_Air);
					}
				}
			}
		}
		save();
	}

	public void update() {
		// Check if chunk has changed, if it has then we need to change what we
		// render#
		if (changed) {
			if (!Globals.isServer()) { // We don't do this on server since we
										// don't draw anything
				// rebuild();
			}
		}
	}

	/**
	 * Only called on client
	 */
	private void marchingCubes() {
		verts = new HashMap<VertexID, Vertex>();
		indicies = new ArrayList<Integer>();
		orderOfVerts = new ArrayList<VertexID>();
		triangles = new ArrayList<Triangle>();

		for (int x = 0; x < size - 1; x++) {
			for (int y = 0; y < sizey - 1; y++) {
				for (int z = 0; z < size - 1; z++) {
					triangulate(x, y, z);
				}
			}
		}
		blocks = null;
		Vector3f U = new Vector3f();
		Vector3f V = new Vector3f();
		Vector3f normal;

		for (Triangle t : triangles) {

			Vector3f.sub(t.p[1], t.p[2], U);
			Vector3f.sub(t.p[0], t.p[2], V);

			float x = (U.y * V.z) - (U.z * V.y);
			float y = (U.z * V.x) - (U.x * V.z);
			float z = (U.x * V.y) - (U.y * V.x);
			normal = new Vector3f(x, y, z);
			try {
				normal.normalise();
			} catch (IllegalStateException e) {
				normal.set(0.f, 0.1f, 0.1f);
				normal.normalise();
			}
			indicies.add(addVertex(t.p[2], normal));
			indicies.add(addVertex(t.p[1], normal));
			indicies.add(addVertex(t.p[0], normal));

		}

		float[] positions = new float[verts.size() * 3];
		// float[] textureCoords = new float[triangles.size() * 6];
		float[] normals = new float[verts.size() * 3];
		int[] int_indicies = new int[indicies.size()];

		int i = 0;
		int j = 0;
		for (VertexID vid : orderOfVerts) {
			Vertex v = verts.get(vid);
			v.averageNormals();

			positions[i++] = v.position.x;
			positions[i++] = v.position.y;
			positions[i++] = v.position.z;

			normals[j++] = v.normal.x;
			normals[j++] = v.normal.y;
			normals[j++] = v.normal.z;
		}
		int k = 0;
		for (int ind : indicies) {
			int_indicies[k++] = ind;
		}

		// Now we can cleanup all the memory we just used!

		for (Vertex v : verts.values()) {
			v.normal = null;
		}
		verts.clear();
		orderOfVerts.clear();
		indicies.clear();
		triangles.clear();

		verts = null;
		orderOfVerts = null;
		indicies = null;
		triangles = null;

		// Add the rendering data for this chunk to a NetworkChunkRenderingData object.
		setCrd(new ChunkRenderingData(positions, int_indicies, normals));
		positions = null;
		normals = null;
		int_indicies = null;

		Globals.getLoadingLock().writeLock().lock();
		Globals.getLoader().addChunkToLoadQueue(this);
		Globals.getLoadingLock().writeLock().unlock();
	}

	private void setCrd(ChunkRenderingData chunkRenderingData) {
		crd = chunkRenderingData;
	}

	private int addVertex(Vector3f position, Vector3f normal) {
		VertexID vid = new VertexID(position);
		if (verts.containsKey(vid)) {
			Vertex vert = verts.get(vid);
			vert.addNormal(normal);
			return vert.id;
		} else {
			Vertex vert = new Vertex(vertID++, position, normal);
			verts.put(vid, vert);
			orderOfVerts.add(vid);
			return vert.id;
		}
	}

	private int triangulate(int x, int y, int z) {

		Vector3f vertlist[] = new Vector3f[12];

		int cubeindex = 0;
		if (blocks[x][y][z].weight < isolevel)
			cubeindex |= 1;
		if (blocks[x + 1][y][z].weight < isolevel)
			cubeindex |= 2;
		if (blocks[x + 1][y][z + 1].weight < isolevel)
			cubeindex |= 4;
		if (blocks[x][y][z + 1].weight < isolevel)
			cubeindex |= 8;
		if (blocks[x][y + 1][z].weight < isolevel)
			cubeindex |= 16;
		if (blocks[x + 1][y + 1][z].weight < isolevel)
			cubeindex |= 32;
		if (blocks[x + 1][y + 1][z + 1].weight < isolevel)
			cubeindex |= 64;
		if (blocks[x][y + 1][z + 1].weight < isolevel)
			cubeindex |= 128;

		if (edgeTable[cubeindex] == 0)
			return (0);

		/*
		 * 0 = blocks[x][y][z].weight 1 = blocks[x+1][y][z].weight 2 = blocks[x+1][y][z+1].weight 3 = blocks[x][y][z+1].weight 4 = blocks[x][y+1][z].weight 5 =
		 * blocks[x+1][y+1][z].weight 6 = blocks[x+1][y+1][z+1].weight 7 = blocks[x][y+1][z+1].weight
		 */

		/* Find the vertices where the surface intersects the cube */
		if ((edgeTable[cubeindex] & 1) != 0)
			vertlist[0] = VertexInterp(new Vector3f(x, y, z), new Vector3f(x + 1, y, z), blocks[x][y][z].weight, blocks[x + 1][y][z].weight);
		if ((edgeTable[cubeindex] & 2) != 0)
			vertlist[1] = VertexInterp(new Vector3f(x + 1, y, z), new Vector3f(x + 1, y, z + 1), blocks[x + 1][y][z].weight, blocks[x + 1][y][z + 1].weight);
		if ((edgeTable[cubeindex] & 4) != 0)
			vertlist[2] = VertexInterp(new Vector3f(x + 1, y, z + 1), new Vector3f(x, y, z + 1), blocks[x + 1][y][z + 1].weight, blocks[x][y][z + 1].weight);
		if ((edgeTable[cubeindex] & 8) != 0)
			vertlist[3] = VertexInterp(new Vector3f(x, y, z + 1), new Vector3f(x, y, z), blocks[x][y][z + 1].weight, blocks[x][y][z].weight);
		if ((edgeTable[cubeindex] & 16) != 0)
			vertlist[4] = VertexInterp(new Vector3f(x, y + 1, z), new Vector3f(x + 1, y + 1, z), blocks[x][y + 1][z].weight, blocks[x + 1][y + 1][z].weight);
		if ((edgeTable[cubeindex] & 32) != 0)
			vertlist[5] = VertexInterp(new Vector3f(x + 1, y + 1, z), new Vector3f(x + 1, y + 1, z + 1), blocks[x + 1][y + 1][z].weight, blocks[x + 1][y + 1][z + 1].weight);
		if ((edgeTable[cubeindex] & 64) != 0)
			vertlist[6] = VertexInterp(new Vector3f(x + 1, y + 1, z + 1), new Vector3f(x, y + 1, z + 1), blocks[x + 1][y + 1][z + 1].weight, blocks[x][y + 1][z + 1].weight);
		if ((edgeTable[cubeindex] & 128) != 0)
			vertlist[7] = VertexInterp(new Vector3f(x, y + 1, z + 1), new Vector3f(x, y + 1, z), blocks[x][y + 1][z + 1].weight, blocks[x][y + 1][z].weight);
		if ((edgeTable[cubeindex] & 256) != 0)
			vertlist[8] = VertexInterp(new Vector3f(x, y, z), new Vector3f(x, y + 1, z), blocks[x][y][z].weight, blocks[x][y + 1][z].weight);
		if ((edgeTable[cubeindex] & 512) != 0)
			vertlist[9] = VertexInterp(new Vector3f(x + 1, y, z), new Vector3f(x + 1, y + 1, z), blocks[x + 1][y][z].weight, blocks[x + 1][y + 1][z].weight);
		if ((edgeTable[cubeindex] & 1024) != 0)
			vertlist[10] = VertexInterp(new Vector3f(x + 1, y, z + 1), new Vector3f(x + 1, y + 1, z + 1), blocks[x + 1][y][z + 1].weight, blocks[x + 1][y + 1][z + 1].weight);
		if ((edgeTable[cubeindex] & 2048) != 0)
			vertlist[11] = VertexInterp(new Vector3f(x, y, z + 1), new Vector3f(x, y + 1, z + 1), blocks[x][y][z + 1].weight, blocks[x][y + 1][z + 1].weight);

		/* Create the triangle */
		int nTriangles = 0;
		for (int i = 0; triTable[cubeindex][i] != -1; i += 3) {
			Triangle t = new Triangle();
			t.p[0] = vertlist[triTable[cubeindex][i]];
			t.p[1] = vertlist[triTable[cubeindex][i + 1]];
			t.p[2] = vertlist[triTable[cubeindex][i + 2]];
			triangles.add(t);

			nTriangles++;
		}

		return (nTriangles);

	}

	/*
	 * Linearly interpolate the position where an isosurface cuts an edge between two vertices, each with their own scalar value
	 */
	Vector3f VertexInterp(Vector3f p1, Vector3f p2, float valp1, float valp2) {

		float mu;
		Vector3f p = new Vector3f();

		if (Math.abs(isolevel - valp1) < 0.00001)
			return (p1);
		if (Math.abs(isolevel - valp2) < 0.00001)
			return (p2);
		if (Math.abs(valp1 - valp2) < 0.00001)
			return (p1);
		mu = (isolevel - valp1) / (valp2 - valp1);
		p.x = p1.x + mu * (p2.x - p1.x);
		p.y = p1.y + mu * (p2.y - p1.y);
		p.z = p1.z + mu * (p2.z - p1.z);

		return (p);
	}

	public PhysicsModel getModel() {
		return model;
	}

	static int edgeTable[] = { 0x0, 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c, 0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00, 0x190, 0x99, 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
			0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90, 0x230, 0x339, 0x33, 0x13a, 0x636, 0x73f, 0x435, 0x53c, 0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30, 0x3a0, 0x2a9, 0x1a3,
			0xaa, 0x7a6, 0x6af, 0x5a5, 0x4ac, 0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0, 0x460, 0x569, 0x663, 0x76a, 0x66, 0x16f, 0x265, 0x36c, 0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963,
			0xa69, 0xb60, 0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff, 0x3f5, 0x2fc, 0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0, 0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55, 0x15c, 0xe5c,
			0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950, 0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc, 0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0, 0x8c0, 0x9c9, 0xac3, 0xbca,
			0xcc6, 0xdcf, 0xec5, 0xfcc, 0xcc, 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0, 0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c, 0x15c, 0x55, 0x35f, 0x256, 0x55a, 0x453, 0x759,
			0x650, 0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc, 0x2fc, 0x3f5, 0xff, 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0, 0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c, 0x36c, 0x265,
			0x16f, 0x66, 0x76a, 0x663, 0x569, 0x460, 0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac, 0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa, 0x1a3, 0x2a9, 0x3a0, 0xd30, 0xc39, 0xf33, 0xe3a, 0x936,
			0x83f, 0xb35, 0xa3c, 0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33, 0x339, 0x230, 0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c, 0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99, 0x190,
			0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c, 0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0 };

	static int triTable[][] = { { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1 }, { 9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1 }, { 9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1 }, { 2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1 },
			{ 8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1 }, { 9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1 }, { 3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1 }, { 1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1 },
			{ 4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1 }, { 4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1 }, { 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1 }, { 5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1 }, { 9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1 }, { 2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1 }, { 10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1 }, { 5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1 }, { 5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1 }, { 0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1 }, { 10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1 },
			{ 8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1 }, { 2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1 }, { 7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1 }, { 2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1 }, { 11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1 }, { 5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1 }, { 11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1 },
			{ 11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1 }, { 9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1 }, { 2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1 }, { 5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1 }, { 6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1 }, { 3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1 }, { 6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1 }, { 1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1 }, { 6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1 }, { 1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1 },
			{ 8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1 }, { 7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1 }, { 3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1 }, { 0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1 }, { 9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1 },
			{ 8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1 }, { 5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1 }, { 0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1 },
			{ 6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1 }, { 10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1 }, { 8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1 }, { 1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1 }, { 0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1 },
			{ 10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1 }, { 0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1 }, { 3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1 },
			{ 6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1 }, { 9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1 }, { 8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1 },
			{ 3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1 }, { 6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1 }, { 10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1 }, { 10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1 }, { 2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1 }, { 7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1 }, { 2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1 },
			{ 1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1 }, { 11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1 }, { 8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1 },
			{ 0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1 }, { 7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1 }, { 10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1 }, { 2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1 }, { 6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1 },
			{ 7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1 }, { 2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1 }, { 10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1 }, { 10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1 },
			{ 0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1 }, { 7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1 }, { 6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1 }, { 8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1 }, { 9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1 },
			{ 6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1 }, { 4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1 },
			{ 10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1 }, { 8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1 }, { 0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1 }, { 1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1 }, { 8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1 },
			{ 10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1 }, { 4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1 }, { 10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1 }, { 5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1 }, { 9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1 }, { 6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1 },
			{ 7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1 }, { 3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1 }, { 7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1 }, { 3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1 }, { 6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1 },
			{ 9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1 }, { 1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1 }, { 4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1 },
			{ 7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1 }, { 6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1 }, { 3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1 },
			{ 0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1 }, { 6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1 },
			{ 0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1 }, { 11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1 }, { 6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1 },
			{ 5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1 }, { 9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1 }, { 1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1 },
			{ 1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1 }, { 10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1 },
			{ 0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1 }, { 10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1 }, { 11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1 }, { 9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1 }, { 7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1 },
			{ 2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1 }, { 8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1 }, { 9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1 },
			{ 9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1 }, { 1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1 },
			{ 9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1 }, { 9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1 },
			{ 5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1 }, { 0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1 }, { 10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1 },
			{ 2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1 }, { 0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1 }, { 0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1 },
			{ 9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1 }, { 5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1 }, { 5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1 }, { 8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1 }, { 9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1 }, { 0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1 }, { 1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1 },
			{ 3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1 }, { 4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1 }, { 9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1 },
			{ 11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1 }, { 11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1 }, { 2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1 },
			{ 9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1 }, { 3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1 }, { 1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1 }, { 4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1 }, { 4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1 }, { 0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1 },
			{ 3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1 }, { 3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1 },
			{ 0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1 }, { 9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1 },
			{ 1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ 0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, { 0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
			{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 } };

	public void setRenderable(boolean b) {
		renderable = b;
	}

	public boolean isRenderable() {
		return renderable;
	}

	public void setModel(PhysicsModel m) {
		model = m;

	}

	public NetworkChunkData getNcd() {
		return ncd;
	}

	public void setNcd(NetworkChunkData ncd) {
		this.ncd = ncd;
	}

	public ChunkRenderingData getCrd() {
		return crd;
	}

}
