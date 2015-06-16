package newRendering;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import newEntities.Camera;
import newEntities.Entity;
import newEntities.Light;
import newEntities.PhysicsEntity;
import newMain.IModule;
import newModels.TexturedModel;
import newModels.TexturedPhysicsModel;
import newShaders.StaticShader;
import newShaders.TerrainShader;
import newSkybox.SkyboxRenderer;
import newTerrains.Terrain;

public class MasterRenderer implements IModule {

	private static final float FOV = 90;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 10000f;

	private static final float RED = 0.5f;
	private static final float GREEN = 0.5f;
	private static final float BLUE = 0.5f;

	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	private Map<TexturedPhysicsModel, List<PhysicsEntity>> entities = new HashMap<TexturedPhysicsModel, List<PhysicsEntity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();

	private SkyboxRenderer skyboxRenderer;

	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void render(List<Light> lights, Camera camera) {
		prepare();
		shader.start();
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		// terrainShader.start();
		// terrainShader.loadSkyColour(RED, GREEN, BLUE);
		// terrainShader.loadLights(lights);
		// terrainShader.loadViewMatrix(camera);
		// terrainRenderer.render(terrains);
		// terrainShader.stop();
		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		terrains.clear();
		entities.clear();
	}

	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public void processEntity(PhysicsEntity entity) {
		TexturedPhysicsModel entityModel = entity.getModel();
		List<PhysicsEntity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<PhysicsEntity> newBatch = new ArrayList<PhysicsEntity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void prepare() {
		glEnable(GL_DEPTH_TEST);
		// glDepthFunc(GL_LEQUAL);
		// glEnable(GL_DEPTH_CLAMP);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}

	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);

		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);

		projectionMatrix.m33 = 0;

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
}
