package main.java.com.ionsystems.infinigen.rendering;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector3f;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.objConverter.Vertex;
import main.java.com.ionsystems.infinigen.physics.PhysicsUtils;
import main.java.com.ionsystems.infinigen.textures.TextureData;

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
import com.bulletphysics.collision.shapes.TriangleMeshShape;
import com.bulletphysics.util.ObjectArrayList;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

@SuppressWarnings("unused")
public class Loader implements IModule {

	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	private HashMap<String, Vector3f> loadedTextures = new HashMap<String, Vector3f>();

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

	public PhysicsModel loadChunkToVAOWithGeneratedPhysics(float[] positions, ObjectArrayList<Vector3f> vertices, float[] textureCoords, float[] normals,
			int[] indices) {
		int vaoID = createVAO();
		ArrayList<Integer> vboIDs = new ArrayList<Integer>();
		vboIDs.add(bindIndicesBuffer(indices));
		vboIDs.add(storeDataInAttributeList(0, 3, positions));
		vboIDs.add(storeDataInAttributeList(1, 2, textureCoords));
		vboIDs.add(storeDataInAttributeList(2, 3, normals));
		unbindVAO();
		// Generate a new physics object to represent the model;
		
		
		ByteBuffer ind = ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder());
		ind.asIntBuffer().put(storeDataInIntBuffer(indices));
		
		ByteBuffer verts = ByteBuffer.allocateDirect(positions.length * 4).order(ByteOrder.nativeOrder());
		verts.asFloatBuffer().put(storeDataInFloatBuffer(positions));
		
		IndexedMesh indexedMesh = new IndexedMesh();
		indexedMesh.indexType = ScalarType.INTEGER;
		indexedMesh.numTriangles = indices.length / 3;
		indexedMesh.numVertices = vertices.size();
		indexedMesh.triangleIndexStride = 12;
		indexedMesh.vertexStride = 4;
		indexedMesh.triangleIndexBase = ind;
		indexedMesh.vertexBase = verts;
		
		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray();
		
		indexVertexArrays.addIndexedMesh(indexedMesh, ScalarType.INTEGER);
		BvhTriangleMeshShape collisionShape = new BvhTriangleMeshShape(indexVertexArrays, true);

		
		return new PhysicsModel(vaoID, vboIDs, indices.length, collisionShape);
	}

	public PhysicsModel loadToVAOWithGeneratedPhysics(float[] positions, List<Vertex> vertices, float[] textureCoords, float[] normals, int[] indices) {
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
		return new PhysicsModel(vaoID, vboIDs, indices.length, simplifiedShape);
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
		// TODO Auto-generated method stub

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
		return new ArrayList<PhysicsEntity>();
	}

}
