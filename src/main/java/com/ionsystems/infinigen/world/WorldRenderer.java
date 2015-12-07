package main.java.com.ionsystems.infinigen.world;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import main.java.com.ionsystems.infinigen.entities.Entity;
import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.models.TexturedModel;
import main.java.com.ionsystems.infinigen.shaders.ChunkShader;
import main.java.com.ionsystems.infinigen.textures.ModelTexture;
import main.java.com.ionsystems.infinigen.textures.TerrainTexture;
import main.java.com.ionsystems.infinigen.textures.TerrainTexturePack;
import main.java.com.ionsystems.infinigen.utility.Maths;

public class WorldRenderer implements IModule {

	private ChunkShader shader;
	// private static int CHUNK_SIZE = 32;
	private TerrainTexture texture;
	int i = 120;

	public WorldRenderer(ChunkShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void renderChunks() {
		int numTriangles = 0;
		bindTextures();
		if (Globals.getLoadedChunks() != null) {
			for (Chunk chunk : Globals.getLoadedChunks()) {
				if (chunk.isRenderable()) {
					numTriangles += chunk.triangles.size();
					loadModelMatrix(chunk);

					renderFace(chunk.getModel());

				}
			}
		}
		//System.out.println("Triangles: " + numTriangles);
	}

	private void renderFace(RawModel rawModel) {
		prepareChunkFace(rawModel);
		// System.out.println(rawModel.getVertexCount());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rawModel.getVertexCount());
		unbindFace();
	}

	private void prepareChunkFace(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		shader.loadShineVariables(1, 0);
	}

	private void bindTextures() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
	}

	private void unbindFace() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Chunk chunk) {
		Vector3f position = new Vector3f(chunk.x * ChunkManager.chunkSize * chunk.blockSize, chunk.y * ChunkManager.chunkSize * chunk.blockSize, chunk.z
				* ChunkManager.chunkSize * chunk.blockSize);
		// System.out.println(position);
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(position, 0, 0, 0, 1, 1, false);
		shader.loadTransformationMatrix(transformationMatrix);
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUp() {
		texture = new TerrainTexture(Globals.getLoader().loadTexture("grassMinecraft"));
	}

	@Override
	public void cleanUp() {
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

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

}
