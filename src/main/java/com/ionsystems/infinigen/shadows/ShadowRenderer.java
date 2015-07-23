package main.java.com.ionsystems.infinigen.shadows;

import java.util.List;
import java.util.Map;

import main.java.com.ionsystems.infinigen.cameras.ICamera;
import main.java.com.ionsystems.infinigen.entities.Light;
import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.models.TexturedModel;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.utility.MatrixHandler;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ShadowRenderer {

	private ShadowShader shader;
	private ShadowFrameBuffers fbos;

	private MatrixHandler depthMatrix;

	public ShadowRenderer(ShadowShader shader, ShadowFrameBuffers fbos) {
		this.shader = shader;

		this.fbos = fbos;
		depthMatrix = new MatrixHandler();
	}

	
	
	public void render(Light sun, ICamera camera, Map<TexturedPhysicsModel, List<PhysicsEntity>> entities) {
		fbos.bindShadowFrameBuffer();
		shader.start();
		prepareRender(sun);

		for (TexturedPhysicsModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<PhysicsEntity> batch = entities.get(model);
			for (PhysicsEntity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
		
		fbos.unbindCurrentFrameBuffer();
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
	}

	private void unbindTexturedModel() {

		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(PhysicsEntity entity) {
		if (entity.isPhysicsBody()) {
			float[] transformationMatrix = entity.updateTransformationMatrixFloat();
			shader.loadDepthModelMatrix(transformationMatrix);
		} else {
			Matrix4f transformationMatrix = entity.updateTransformationMatrix();
			shader.loadDepthModelMatrix(transformationMatrix);
		}
	}

	private void prepareRender(Light light) {
		fbos.bindShadowFrameBuffer();

		MatrixHandler depthProjectionMatrix = new MatrixHandler();
		MatrixHandler depthViewMatrix = new MatrixHandler();

		Vector3f normLightPosition = new Vector3f();
		light.getPosition().normalise(normLightPosition);

		depthProjectionMatrix.initOrthographicMatrix(-20, 20, -20, 20, -20, 40);
		depthViewMatrix.lookAt(normLightPosition, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

		Matrix4f.mul(depthProjectionMatrix, depthViewMatrix, depthMatrix);

		
		shader.loadDepthMatrix(depthMatrix);

	}

}
