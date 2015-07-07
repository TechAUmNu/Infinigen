package engineTester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import newEntities.ICamera;
import newEntities.RTSCamera;
import newEntities.ThirdPersonCamera;
import newEntities.Light;
import newEntities.PhysicsEntity;
import newEntities.Player;
import newGui.GuiManager;
import newMain.Globals;
import newMain.IModule;
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
import newUnitBuilder.UnitBuilderManager;
import newUtility.MousePicker;
import newUtility.OSValidator;
import newWorld.ChunkManager;
import newobjConverter.OBJFileLoader;

public class MainGameLoop {

	/**
	 * Globals that are used a lot go here.
	 */
	
	private int keyDelayTime = 20;
	private int keyTimer = 0;
	
	
	private GuiManager gui;
	private Loader loader;
	private PhysicsManager physics;
	private MousePicker picker;
	private ICamera activeCamera;
	private int activeCameraID;
	private ThirdPersonCamera thirdPersonCamera;
	private RTSCamera rtsCamera;
	private Player player;
	private MasterRenderer renderer;
	private boolean mouse0, mouse1 = false;
	private List<PhysicsEntity> entities;
	private List<Light> lights;
	private Terrain currentTerrain;
	private UnitBuilderManager unitBuilder;

	private List<IModule> loadedModules, unloadedModules;
	private ChunkManager world;

	/**
	 * Main entry point to the game
	 * 
	 * @param args
	 *            Arguments
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

		// setUpTerrain();
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

		loadedModules = new ArrayList<IModule>();

		loader = new Loader();
		gui = new GuiManager(loader);

		renderer = new MasterRenderer(loader);
		physics = new PhysicsManager();
		entities = new ArrayList<PhysicsEntity>();
		lights = new ArrayList<Light>();
		
		world = new ChunkManager();
		
		unitBuilder = new UnitBuilderManager();

		physics.setUp();

		PhysicsModel pmodel = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics("box", loader);
		TexturedPhysicsModel testPhysics = new TexturedPhysicsModel(pmodel, new ModelTexture(loader.loadTexture("white")));
		player = new Player(testPhysics, new Vector3f(0, (float) -1, 0), 0, 0, 0, 1, physics.getProcessor());

		thirdPersonCamera = new ThirdPersonCamera(player);
		rtsCamera = new RTSCamera();
		activeCamera = rtsCamera;
		picker = new MousePicker(activeCamera, renderer.getProjectionMatrix());

		// Core modules
		loadedModules.add(loader);
		loadedModules.add(gui);
		loadedModules.add(renderer);
		loadedModules.add(physics);

		loadedModules.add(player);
		//loadedModules.add(thirdPersonCamera);
		loadedModules.add(rtsCamera);
		activeCameraID = 2;
		loadedModules.add(picker);
		loadedModules.add(unitBuilder);
		loadedModules.add(world);

		// Add anything to the globals that might be needed elsewhere.
		Globals.setLoader(loader);


		for (IModule module : loadedModules) {
			module.setUp();
		}

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
	}

	/**
	 * Called every frame Put anything that needs updated each frame here
	 */
	private void update() {
		DisplayManager.updateDisplay();

		for (IModule module : loadedModules) {
			module.update();
		}
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
		
		if(Keyboard.isKeyDown(Keyboard.KEY_C)){
			if(keyTimer < 0){
				switchCamera();
			}
		}
		keyTimer--;

		for (IModule module : loadedModules) {
			module.process();
		}
		
		
	}

	private void switchCamera() {		
		if(activeCameraID == 1){
			loadedModules.remove(thirdPersonCamera);
			loadedModules.add(rtsCamera);
			activeCamera = rtsCamera;
			activeCameraID = 2;
			keyTimer = keyDelayTime;
		}
		else if(activeCameraID == 2){
			loadedModules.remove(rtsCamera);
			loadedModules.add(thirdPersonCamera);
			activeCamera = thirdPersonCamera;
			activeCameraID = 1;
			keyTimer = keyDelayTime;
		}		
	}

	/**
	 * Called every frame Put anything that needs added to the render here
	 */
	private void prepareRender() {
		entities.clear();

		renderer.processEntity(player);
		
		for(IModule module : loadedModules){
			ArrayList<PhysicsEntity> toAdd = module.prepare();
			if(toAdd != null){
				entities.addAll(toAdd);
			}
		}

		for (PhysicsEntity entity : entities) {
			renderer.processEntity(entity);
		}
	}

	/**
	 * Called every frame Put any renderer here
	 */
	private void render() {

		renderer.render(lights, activeCamera);
		gui.render();

		//for (IModule module : loadedModules) {
		///	module.render();
		//}
	}

	private void cleanUp() {
		gui.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();

		for (IModule module : loadedModules) {
			module.cleanUp();
		}
		DisplayManager.closeDisplay();
		System.exit(0);
	}

}
