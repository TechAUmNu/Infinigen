package main.java.com.ionsystems.infinigen.rendering;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import javax.vecmath.Vector3f;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.objConverter.Vertex;
import main.java.com.ionsystems.infinigen.physics.PhysicsUtils;
import main.java.com.ionsystems.infinigen.textures.TextureData;
import main.java.com.ionsystems.infinigen.world.ChunkManager;
import main.java.com.ionsystems.infinigen.world.ChunkRenderingData;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.util.ObjectArrayList;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

@SuppressWarnings("unused")
public class Loader implements IModule {

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	private HashMap<String, Vector3f> loadedTextures = new HashMap<String, Vector3f>();
	private ArrayList<ChunkRenderingData> chunkLoadingQueue = new ArrayList<ChunkRenderingData>();
	private ArrayList<PhysicsModel> modelUnloadingQueue = new ArrayList<PhysicsModel>();

	private int bufferAllocationCount = 0;

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(bindIndicesBuffer(indices));
		vboIDs.add(storeDataInAttributeList(0, 3, positions));
		vboIDs.add(storeDataInAttributeList(1, 2, textureCoords));
		vboIDs.add(storeDataInAttributeList(2, 3, normals));
		unbindVAO();
		return new RawModel(vaoID, vboIDs, indices.length);
	}

	public RawModel loadToVAO(float[] positions, float[] normals, int[] indices) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(bindIndicesBuffer(indices));
		vboIDs.add(storeDataInAttributeList(0, 3, positions));
		vboIDs.add(storeDataInAttributeList(1, 3, normals));
		unbindVAO();
		return new RawModel(vaoID, vboIDs, indices.length);
	}

	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int dimensions) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();

		vboIDs.add(storeDataInAttributeList(0, 3, positions));
		vboIDs.add(storeDataInAttributeList(1, 2, textureCoords));
		vboIDs.add(storeDataInAttributeList(2, 3, normals));
		unbindVAO();
		return new RawModel(vaoID, vboIDs, positions.length / dimensions);
	}

	public int loadToVAO(float[] vertexPositions, float[] textureCoords) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(storeDataInAttributeList(0, 2, vertexPositions));
		vboIDs.add(storeDataInAttributeList(1, 2, textureCoords));
		unbindVAO();
		return vaoID;
	}

	// public RawModel loadToVAO(float[] positions, float[] textureCoords,
	// float[] normals, int vertexCount) {
	// int vaoID = createVAO();
	// /
	// storeDataInAttributeList(0, 3, positions);
	// storeDataInAttributeList(1, 2, textureCoords);
	// storeDataInAttributeList(2, 3, normals);
	// unbindVAO();
	// return new RawModel(vaoID, vertexCount);
	// }

	public PhysicsModel loadChunkToVAOWithGeneratedPhysics(float[] positions, float[] normals, int[] indices) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(bindIndicesBuffer(indices));
		vboIDs.add(storeDataInAttributeList(0, 3, positions));
		vboIDs.add(storeDataInAttributeList(1, 3, normals));
		unbindVAO();
		// Generate a new physics object to represent the model;

		ByteBuffer ind = ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder());
		for (int i : indices) {
			ind.putInt(i);
		}
		ind.flip();
		ByteBuffer verts = ByteBuffer.allocateDirect(positions.length * 4).order(ByteOrder.nativeOrder());

		for (Float f : positions) {
			verts.putFloat(f);
		}
		verts.flip();

		IndexedMesh indexedMesh = new IndexedMesh();
		indexedMesh.indexType = ScalarType.INTEGER;
		indexedMesh.numTriangles = indices.length / 3;
		indexedMesh.numVertices = positions.length / 3;
		indexedMesh.triangleIndexStride = 3 * 4;
		indexedMesh.vertexStride = 3 * 4;
		indexedMesh.triangleIndexBase = ind;
		indexedMesh.vertexBase = verts;

		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray();

		indexVertexArrays.addIndexedMesh(indexedMesh, ScalarType.INTEGER);
		BvhTriangleMeshShape collisionShape = new BvhTriangleMeshShape(indexVertexArrays, true);

		return new PhysicsModel(vaoID, vboIDs, indices.length, collisionShape);
	}

	public PhysicsModel loadToVAOWithGeneratedPhysics(float[] positions, List<Vertex> vertices, float[] textureCoords, float[] normals, int[] indices,
			String objFile) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(bindIndicesBuffer(indices));
		vboIDs.add(storeDataInAttributeList(0, 3, positions));
		vboIDs.add(storeDataInAttributeList(1, 2, textureCoords));
		vboIDs.add(storeDataInAttributeList(2, 3, normals));
		unbindVAO();
		// Generate a new physics object to represent the model;
		ObjectArrayList<Vector3f> verticesPhysics = new ObjectArrayList<Vector3f>();

		for (Vertex v : vertices) {
			Vector3f mathVector = new Vector3f();
			mathVector.x = v.getPosition().getX();
			mathVector.y = v.getPosition().getY();
			mathVector.z = v.getPosition().getZ();
			verticesPhysics.add(mathVector);
		}
		ConvexHullShape collisionShape = new ConvexHullShape(verticesPhysics);
		// Now simplify the shape to speed up physics
		ConvexHullShape simplifiedShape = PhysicsUtils.simplifyConvexShape(collisionShape);

		PhysicsModel model = new PhysicsModel(vaoID, vboIDs, indices.length, simplifiedShape);

		// Add the model parameters
		try (Stream<String> lines = Files.lines(new File(objFile + ".imo").toPath(), StandardCharsets.UTF_8)) {
			for (String line : (Iterable<String>) lines::iterator) {
				System.out.println(line);
				String[] splitLine = line.split("=");
				String parameterName = splitLine[0];
				String parameterValue = splitLine[1];
				switch (parameterName) {
				case "name":
					model.setName(parameterValue);
					break;
				case "description":
					model.setDescription(parameterValue);
					break;
				case "sizex":
					model.setSizeX(Float.parseFloat(parameterValue));
					break;
				case "sizey":
					model.setSizeY(Float.parseFloat(parameterValue));
					break;
				case "sizez":
					model.setSizeZ(Float.parseFloat(parameterValue));
					break;
				case "scale":
					model.setScale(Float.parseFloat(parameterValue));
					break;
				case "mass":
					model.setMass(Float.parseFloat(parameterValue));
					break;
				case "texture":
					model.setTexture(parameterValue);
					break;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

	public RawModel loadToVAO(float[] positions, int dimensions) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(storeDataInAttributeList(0, dimensions, positions));
		unbindVAO();
		return new RawModel(vaoID, vboIDs, positions.length / dimensions);
	}

	public RawModel loadToVAO(float[] positions, float[] textureCoords, int dimensions) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(storeDataInAttributeList(0, dimensions, positions));
		vboIDs.add(storeDataInAttributeList(1, 2, textureCoords));
		unbindVAO();
		return new RawModel(vaoID, vboIDs, positions.length / dimensions);
	}

	public Vector3f loadTexture(String fileName) {
		if (loadedTextures.containsKey(fileName)) {
			return loadedTextures.get(fileName);
		}

		Texture texture = null;
		Vector3f WidthHeightID = new Vector3f(0, 0, 0);
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/textures/" + fileName + ".png"));
			WidthHeightID.y = texture.getImageHeight();
			WidthHeightID.x = texture.getImageWidth();
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		WidthHeightID.z = textureID;

		loadedTextures.put(fileName, WidthHeightID);
		textures.add(textureID);
		return WidthHeightID;
	}

	public int loadCubeMap(String[] textureFiles) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < textureFiles.length; i++) {
			TextureData data = decodeTextureFile("res/textures/" + textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textures.add(texID);
		return texID;
	}

	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}

	@Override
	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	private int storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}

	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	private int bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		return vboID;
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {

		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	@Override
	public void process() {
		// Here we process all the models, etc to be loaded/unloaded
		// We will only allocate a few ms to this each frame to stop the
		// framerate from dipping when lots of stuff is happening.

		// Also need to be thread safe
		long startTime = System.nanoTime();
		if (Globals.getLoadingLock().readLock().tryLock()) {
			ArrayList<ChunkRenderingData> crdRemove = new ArrayList<ChunkRenderingData>();
			for (ChunkRenderingData crd : chunkLoadingQueue) {
				PhysicsModel m = loadChunkToVAOWithGeneratedPhysics(crd.positions, crd.normals, crd.indicies);
				crd.c.setModel(m);
				crd.c.setRenderable(true);
				m.generateWorldRigidBody();
				m.getBody().translate(new Vector3f(crd.c.x * ChunkManager.chunkSize, crd.c.y * ChunkManager.chunkSize, crd.c.z * ChunkManager.chunkSize));
				Globals.getPhysics().getProcessor().addRigidBody(m.getBody());

				crdRemove.add(crd);
				long elapsedTime = System.nanoTime() - startTime;
				if (elapsedTime > 100000)
					break;
			}
			chunkLoadingQueue.removeAll(crdRemove);
			Globals.getLoadingLock().readLock().unlock();
		}
		if (Globals.getUnloadingLock().readLock().tryLock()) {
			startTime = System.nanoTime();
			ArrayList<RawModel> rmRemove = new ArrayList<RawModel>();
			for (PhysicsModel rm : modelUnloadingQueue) {
				rm.cleanUp();
				Globals.getPhysics().getProcessor().removeRigidBody(rm.getBody());

				rmRemove.add(rm);
				long elapsedTime = System.nanoTime() - startTime;
				if (elapsedTime > 100000)
					break;
			}
			modelUnloadingQueue.removeAll(rmRemove);
			Globals.getUnloadingLock().readLock().unlock();
		}

	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addChunkToLoadQueue(ChunkRenderingData crd) {
		chunkLoadingQueue.add(crd);
	}

	public void addModelToUnloadQueue(PhysicsModel model) {

		modelUnloadingQueue.add(model);
	}

}
