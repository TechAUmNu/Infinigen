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
import newEntities.PhysicsEntity;
import newMain.Globals;
import newMain.IModule;
import newModels.RawModel;
import newModels.TexturedModel;
import newShaders.ChunkShader;
import newTerrains.Terrain;
import newTextures.ModelTexture;
import newTextures.TerrainTexture;
import newTextures.TerrainTexturePack;
import newUtility.Maths;

public class WorldRenderer implements IModule {

	private ChunkShader shader;
	//private static int CHUNK_SIZE = 32;
	private TerrainTexture texture;
	

	public WorldRenderer(ChunkShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);		
		shader.stop();
		
		
	}

	public void renderChunks() {
		bindTextures();
		for (Chunk chunk : Globals.getLoadedChunks()) {		
			
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
		//System.out.println(rawModel.getVertexCount());
		GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
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
		Vector3f position = new Vector3f(chunk.x * chunk.size * chunk.blockSize,chunk.y * chunk.size * chunk.blockSize, chunk.z * chunk.size * chunk.blockSize);
		//System.out.println(position);
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
