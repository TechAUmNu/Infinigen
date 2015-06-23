package newWorld;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import newEntities.Entity;
import newModels.RawModel;
import newModels.TexturedModel;
import newShaders.ChunkShader;
import newTerrains.Terrain;
import newTextures.ModelTexture;
import newTextures.TerrainTexturePack;
import newUtility.Maths;

public class WorldRenderer {

	private ChunkShader shader;
	private static int CHUNK_SIZE = 32;

	public WorldRenderer(ChunkShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(ArrayList<Chunk> visibleChunks) {
		for (Chunk chunk : visibleChunks) {
			
			
			loadModelMatrix(chunk);
			renderFace(chunk.getBottomModel());
			renderFace(chunk.getTopModel());
			renderFace(chunk.getBackModel());
			renderFace(chunk.getFrontModel());
			renderFace(chunk.getLeftModel());
			renderFace(chunk.getRightModel());
			
			
			
			
		}
	}

	private void renderFace(RawModel rawModel) {
		prepareChunkFace(rawModel);		
		GL11.glDrawArrays(GL11.GL_QUADS, 0, rawModel.getVertexCount());
		unbindChunk();
	}

	private void prepareChunkFace(RawModel rawModel) {		
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		//bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}

	private void bindTextures(Chunk terrain) {
		/*
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
		*/
	}

	private void unbindChunk() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Chunk chunk) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(chunk.x * CHUNK_SIZE, chunk.y * CHUNK_SIZE, chunk.z * CHUNK_SIZE), 0, 0, 0, 1, 1, false);
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
