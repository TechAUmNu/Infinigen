package engineTester;

import java.util.ArrayList;
import java.util.List;
import objConverter.OBJFileLoader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import newEntities.Camera;
import newEntities.Light;
import newEntities.PhysicsEntity;
import newEntities.Player;
import newGui.GuiManager;
import newModels.PhysicsModel;
import newModels.TexturedModel;
import newModels.TexturedPhysicsModel;
import newPhysics.PhysicsManager;
import newRendering.DisplayManager;
import newRendering.Loader;
import newRendering.MasterRenderer;
import newTerrains.Terrain;
import newTextures.ModelTexture;
import newTextures.TerrainTexture;
import newTextures.TerrainTexturePack;
import newUtility.MousePicker;
import newUtility.OSValidator;

public class MainGameLoop {

	/**
	 * Globals that are used alot go here.
	 */
	private GuiManager gui;
	private Loader loader;
	private PhysicsManager physics;
	private MousePicker picker;
	private Camera camera;
	private Player player;
	private MasterRenderer renderer;
	private boolean mouse0, mouse1 = false;
	private List<PhysicsEntity> entities;
	private List<Light> lights;
	private Terrain currentTerrain;

	/**
	 * Main entry point to the game
	 * 
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		MainGameLoop g = new MainGameLoop();
		g.setup();
		g.mainGame();
		g.cleanUp();
	}

	/**
	 * Main Game method
	 */
	public void mainGame() {
		loadAssets();

		setUpTerrain();
		generateGui();

		while (!Display.isCloseRequested()) {

			update();
			process();
			prepareRender();
			render();

		}

		cleanUp();

	}

	/**
	 * Setup
	 * 
	 * Put anything global that needs initialised here
	 */
	private void setup() {
		OSValidator.setCorrectNativesLocation();
		DisplayManager.createDisplay();

		loader = new Loader();
		gui = new GuiManager(loader);
		renderer = new MasterRenderer(loader);
		physics = new PhysicsManager();
		entities = new ArrayList<PhysicsEntity>();
		lights = new ArrayList<Light>();

		PhysicsModel pmodel = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics("box", loader);
		TexturedPhysicsModel testPhysics = new TexturedPhysicsModel(pmodel, new ModelTexture(loader.loadTexture("white")));
		player = new Player(testPhysics, new Vector3f(100, 0, -50), 0, 0, 0, 1, physics.getProcessor());

		camera = new Camera(player);
		picker = new MousePicker(camera, renderer.getProjectionMatrix());

	}

	/**
	 * Put anything to do with loading models etc here
	 */
	private void loadAssets() {
		lights.add(new Light(new Vector3f(0, 10000, -7000), new Vector3f(1, 1, 1))); // Sun

	}

	/**
	 * Anything to do with setting up the terrain
	 */
	private void setUpTerrain() {
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		currentTerrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");
	}

	/**
	 * Anything to do with setting up the gui
	 */
	private void generateGui() {
		gui.generateElement(0, 0, "uvgrid01");
		gui.generateElement(1024, 0, "uvgrid01");
	}

	/**
	 * Called every frame Put anything that needs updated each frame here
	 */
	private void update() {
		DisplayManager.updateDisplay();
		camera.move(currentTerrain);
		player.move(currentTerrain);
		physics.simulate();
		picker.update();
	}

	/**
	 * Called every frame Put anything to do with the user here
	 */
	private void process() {
		if (Mouse.isButtonDown(1) && !mouse1) {
			Mouse.setGrabbed(true);
			mouse1 = true;

		}

		// Process Mouse events
		while (Mouse.next()) {
			if (Mouse.getEventButton() == 1) {
				if (!Mouse.getEventButtonState()) {
					Mouse.setGrabbed(false);
					mouse1 = false;
				}
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			cleanUp();
		}
	}

	/**
	 * Called every frame Put anything that needs added to the render here
	 */
	private void prepareRender() {

		renderer.processEntity(player);
		renderer.processTerrain(currentTerrain);

		for (PhysicsEntity entity : entities) {
			renderer.processEntity(entity);
		}
	}

	/**
	 * Called every frame Put any renderers here
	 */
	private void render() {
		renderer.render(lights, camera);
		gui.render();
	}

	private void cleanUp() {
		gui.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		System.exit(0);
	}

}
