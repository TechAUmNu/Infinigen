package main.java.com.ionsystems.infinigen.rendering;

import java.util.List;
import java.util.Map;

import main.java.com.ionsystems.infinigen.entities.Entity;
import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.models.TexturedModel;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.shaders.StaticShader;
import main.java.com.ionsystems.infinigen.textures.ModelTexture;
import main.java.com.ionsystems.infinigen.utility.Maths;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class EntityRenderer {

	private Matrix4f projectionMatrix;
	private StaticShader shader;

	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		this.projectionMatrix = projectionMatrix;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Map<TexturedPhysicsModel, List<PhysicsEntity>> entities) {
		for (TexturedPhysicsModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<PhysicsEntity> batch = entities.get(model);
			for (PhysicsEntity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLighting(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(PhysicsEntity entity) {
		shader.highlightEntity(false);
		if (entity.isPhysicsBody()) {
			float[] transformationMatrix = entity.updateTransformationMatrixFloat();
			shader.loadTransformationMatrix(transformationMatrix);
			if(entity.isHighlighted()){			
				shader.highlightEntity(true);
			}
		} else {
			Matrix4f transformationMatrix = entity.updateTransformationMatrix();
			shader.loadTransformationMatrix(transformationMatrix);
		}
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}

}
