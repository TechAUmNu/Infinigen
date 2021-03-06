package main.java.com.ionsystems.infinigen.shadows;

import java.util.List;
import java.util.Map;

import main.java.com.ionsystems.infinigen.cameras.ICamera;
import main.java.com.ionsystems.infinigen.entities.Light;
import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.RawModel;
import main.java.com.ionsystems.infinigen.models.TexturedModel;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.utility.MatrixHandler;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ShadowRenderer {

	private ShadowShader shader;

	private ShadowFrameBuffers fbos;

	private MatrixHandler depthMatrix;
	int calls = 0;
//	private Vector3f position = new Vector3f();

	public ShadowMap getShadowMap() {
		MatrixHandler biasMatrix = new MatrixHandler();
		MatrixHandler depthBiasMatrix = new MatrixHandler();
		biasMatrix.setBias();
		Matrix4f.mul(biasMatrix, depthMatrix, depthBiasMatrix);

		return new ShadowMap(depthBiasMatrix, (int) fbos.getDepthTexture().z);
	}

	public ShadowRenderer(ShadowShader shader, ShadowFrameBuffers fbos) {
		this.shader = shader;

		this.fbos = fbos;
		depthMatrix = new MatrixHandler();
	}

	public void render(Light sun, ICamera camera, Map<TexturedPhysicsModel, List<PhysicsEntity>> entities) {
		glCullFace(GL_FRONT);
		fbos.bindShadowFrameBuffer();
		shader.start();
		prepareRender(sun);

		for (TexturedPhysicsModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<PhysicsEntity> batch = entities.get(model);
			for (PhysicsEntity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getPhysicsModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}

		// Render terrain
//		if (Globals.getLoadedChunks() != null) {
//			for (Chunk chunk : Globals.getLoadedChunks()) {
//				if (chunk.isRenderable()) {
//					loadModelMatrix(chunk);
//					renderFace(chunk.getModel());
//				}
//			}
//		}

		shader.stop();

		fbos.unbindCurrentFrameBuffer();
		glCullFace(GL_BACK);
	}

//	private void renderFace(RawModel rawModel) {
//		prepareChunkFace(rawModel);
//		// System.out.println(rawModel.getVertexCount());
//		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rawModel.getVertexCount());
//		unbindFace();
//	}

//	private void prepareChunkFace(RawModel rawModel) {
//		GL30.glBindVertexArray(rawModel.getVaoID());
//		GL20.glEnableVertexAttribArray(0);
//
//	}
//
//	private void unbindFace() {
//		GL20.glDisableVertexAttribArray(0);
//		GL30.glBindVertexArray(0);
//	}
//
//	private void loadModelMatrix(Chunk chunk) {
//
//		position.x = chunk.x * chunk.size * chunk.blockSize;
//		position.y = chunk.y * chunk.size * chunk.blockSize;
//		position.z = chunk.z * chunk.size * chunk.blockSize;
//
//		Matrix4f transformationMatrix = Maths.createTransformationMatrix(position, 0, 0, 0, 1, 1, false);
//		shader.loadDepthModelMatrix(transformationMatrix);
//	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getPhysicsModel();
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

		// Hooray it works!
		Vector3f cameraPosition = Globals.getCameraPosition();

		depthProjectionMatrix.initOrthographicMatrix(-500 - cameraPosition.z, 500 - cameraPosition.z, -500, 500, -500 - cameraPosition.x,
				500 - cameraPosition.x);

		depthViewMatrix.lookAt(light.getPosition().normalise(new Vector3f()), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
		// depthViewMatrix.setPosition(Globals.getCameraPosition().normalise(new
		// Vector3f()));
		Matrix4f.mul(depthProjectionMatrix, depthViewMatrix, depthMatrix);

		shader.loadDepthMatrix(depthMatrix);

	}

}
