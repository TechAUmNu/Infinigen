package main.java.com.ionsystems.infinigen.main;

import java.util.ArrayList;
import java.util.List;

import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import main.java.com.ionsystems.infinigen.audio.AudioManager;
import main.java.com.ionsystems.infinigen.cameras.ICamera;
import main.java.com.ionsystems.infinigen.cameras.RTSCamera;
import main.java.com.ionsystems.infinigen.cameras.ThirdPersonCamera;
import main.java.com.ionsystems.infinigen.entities.Light;
import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.gui.GuiManager;
import main.java.com.ionsystems.infinigen.networking.NetworkingManager;
import main.java.com.ionsystems.infinigen.physics.PhysicsManager;
import main.java.com.ionsystems.infinigen.rendering.DisplayManager;
import main.java.com.ionsystems.infinigen.rendering.Loader;
import main.java.com.ionsystems.infinigen.rendering.MasterRenderer;
import main.java.com.ionsystems.infinigen.shadows.ShadowFrameBuffers;
import main.java.com.ionsystems.infinigen.shadows.ShadowShader;
import main.java.com.ionsystems.infinigen.text.TextManager;
import main.java.com.ionsystems.infinigen.unitBuilder.UnitBuilderManager;
import main.java.com.ionsystems.infinigen.utility.MousePicker;
import main.java.com.ionsystems.infinigen.utility.OSValidator;
import main.java.com.ionsystems.infinigen.water.WaterFrameBuffers;
import main.java.com.ionsystems.infinigen.water.WaterRenderer;
import main.java.com.ionsystems.infinigen.water.WaterShader;
import main.java.com.ionsystems.infinigen.water.WaterTile;
import main.java.com.ionsystems.infinigen.world.ChunkManager;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.bulletphysics.dynamics.RigidBody;

public class Main {

	/**
	 * Globals that are used a lot go here.
	 */

	private int keyDelayTime = 20;
	private int keyTimer = 0;
	private boolean gameEnd;

	private GuiManager gui;
	private Loader loader;
	private PhysicsManager physics;
	private MousePicker picker;
	private TextManager text;
	private ICamera activeCamera;
	private int activeCameraID;
	private ThirdPersonCamera thirdPersonCamera;
	private RTSCamera rtsCamera;
	private MasterRenderer renderer;
	private boolean mouse1 = false;

	private List<Light> lights;
	private Light sun;
	private UnitBuilderManager unitBuilder;
	private NetworkingManager networking;

	private List<IModule> loadedModules;
	private ChunkManager world;
	private WaterFrameBuffers fbos;
	private WaterShader waterShader;
	private WaterRenderer waterRenderer;
	private List<WaterTile> waters;

	private ShadowFrameBuffers sfbos;
	private int debugKeyTimer;

	/**
	 * Main entry point to the game
	 * 
	 * @param args
	 *            Arguments
	 */
	public static void main(String[] args) {

		Main g = new Main();
		if (args.length > 0) {
			if (args[0].equals("server")) {
				Globals.setPort(Integer.parseInt(args[1]));
				Globals.setServer(true);
			} else if (args[0].equals("client")) {
				Globals.setServer(false);
				Globals.setIp(args[1]);
				Globals.setPort(Integer.parseInt(args[2]));
			}

		}

		Globals.setLoading(true);
		g.setup();
		g.mainGame();
		g.cleanUp();
	}

	/**
	 * Main Game method
	 */
	public void mainGame() {
		if (!Globals.isServer()) {
			loadAssets();

			// setUpTerrain();

		}
		Globals.setLoading(false);

		if (!Globals.isServer()) {

			fbos = new WaterFrameBuffers();
			waterShader = new WaterShader();
			waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);

			waters = new ArrayList<WaterTile>();
			waters.add(new WaterTile(75, -75, 0));

			generateGui();

			while (!Display.isCloseRequested()) {

				update();
				process();
				prepareRender();
				render();

			}
		} else {
			while (!gameEnd) {
				update();
			}
		}
		cleanUp();

	}

	/**
	 * Setup
	 * 
	 * Put anything global that needs initialised here
	 */
	private void setup() {
		if (!Globals.isServer()) {
			OSValidator.setCorrectNativesLocation();
			DisplayManager.createDisplay();

			loadedModules = new ArrayList<IModule>();

			loader = new Loader();
			gui = new GuiManager(loader);

			sfbos = new ShadowFrameBuffers();
			renderer = new MasterRenderer(loader, sfbos);
			physics = new PhysicsManager();

			lights = new ArrayList<Light>();

			world = new ChunkManager();

			unitBuilder = new UnitBuilderManager();
			text = new TextManager();
			physics.setUp();
			Globals.setPhysics(physics);
			Globals.setDebugRendering(false);

			thirdPersonCamera = new ThirdPersonCamera();
			rtsCamera = new RTSCamera();
			activeCamera = rtsCamera;
			picker = new MousePicker(renderer.getProjectionMatrix());
			picker.setCamera(rtsCamera);
			// Networking
			networking = new NetworkingManager();

			// Core modules
			loadedModules.add(loader);
			loadedModules.add(gui);
			loadedModules.add(renderer);
			Globals.setRenderer(renderer);
			loadedModules.add(physics);

			// Cameras
			// loadedModules.add(thirdPersonCamera);
			loadedModules.add(rtsCamera);
			activeCameraID = 2;
			Globals.setActiveCameraID(2);
			Globals.setActiveCamera(activeCamera);
			loadedModules.add(picker);
			loadedModules.add(unitBuilder);
			loadedModules.add(world);
			loadedModules.add(networking);
			loadedModules.add(text);

			// Add anything to the globals that might be needed elsewhere.
			Globals.setLoader(loader);
			// loadAudio();

			for (IModule module : loadedModules) {
				module.setUp();
			}
		} else {

			loadedModules = new ArrayList<IModule>();

			// loader = new Loader();
			physics = new PhysicsManager();

			world = new ChunkManager();
			physics.setUp();
			Globals.setPhysics(physics);
			// Networking
			Globals.setBodies(new ArrayList<RigidBody>());
			networking = new NetworkingManager();

			// Core modules
			// loadedModules.add(loader);
			loadedModules.add(physics);
			loadedModules.add(world);
			loadedModules.add(networking);

			// Add anything to the globals that might be needed elsewhere.
			Globals.setLoader(loader);

			for (IModule module : loadedModules) {
				module.setUp();
			}
		}

	}

	private void loadAudio() {

		MaryInterface marytts;
		try {
			marytts = new LocalMaryInterface();
			Set<String> voices = marytts.getAvailableVoices();

			marytts.setVoice(voices.iterator().next());
			AudioInputStream audio = marytts.generateAudio("Hello world.");
			AudioPlayer player = new AudioPlayer(audio);
			player.start();
			player.join();
		} catch (MaryConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SynthesisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AudioManager.loadWAVAudioFile("stalker.wav");

		AudioManager.playMusic("stalker.wav", 1.0f, 1.0f, false);

	}

	/**
	 * Put anything to do with loading models etc here
	 */
	private void loadAssets() {

		sun = new Light(new Vector3f(activeCamera.getPosition().x + 5000, 3000, activeCamera.getPosition().z), new Vector3f(1, 1, 1));
		lights.add(sun); // Sun

	}

	/**
	 * Anything to do with setting up the gui
	 */
	private void generateGui() {
		//gui.addElement(0, 0, sfbos.getDepthTexture());

	}

	/**
	 * Called every frame Put anything that needs updated each frame here
	 */
	private void update() {
		if (!Globals.isServer()) {
			DisplayManager.updateDisplay();
		}

		for (IModule module : loadedModules) {
			module.update();
		}

		// sun.setPosition(new Vector3f(activeCamera.getPosition().x + 500,
		// 500,activeCamera.getPosition().z + 500));

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

		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			if (keyTimer < 0) {
				switchCamera();
			}
		}
		keyTimer--;

		for (IModule module : loadedModules) {
			module.process();
		}

		while (Keyboard.next()) {
			if (!Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_P) {

					ChunkManager.loadDistance++;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_L) {
					ChunkManager.loadDistance--;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_O) {

					if (!Globals.debugRendering()) {
						Globals.setDebugRendering(true);

					} else {
						Globals.setDebugRendering(false);

					}

				}
			}
		}

	}

	private void switchCamera() {
		if (activeCameraID == 1) {
			loadedModules.remove(thirdPersonCamera);
			loadedModules.add(rtsCamera);
			activeCamera = rtsCamera;
			activeCameraID = 2;

			keyTimer = keyDelayTime;
		} else if (activeCameraID == 2) {
			loadedModules.remove(rtsCamera);
			loadedModules.add(thirdPersonCamera);
			activeCamera = thirdPersonCamera;
			activeCameraID = 1;
			keyTimer = keyDelayTime;
		}
		picker.setCamera(activeCamera);
		Globals.setActiveCameraID(activeCameraID);
		Globals.setActiveCamera(activeCamera);
	}

	/**
	 * Called every frame Put anything that needs added to the render here
	 */
	private void prepareRender() {
		ArrayList<PhysicsEntity> entitiesToRender = new ArrayList<PhysicsEntity>();

		for (IModule module : loadedModules) {
			ArrayList<PhysicsEntity> toAdd = module.prepare();
			if (toAdd != null) {
				entitiesToRender.addAll(toAdd);
			}
		}

		entitiesToRender.addAll(Globals.getEntities());

		for (PhysicsEntity entity : entitiesToRender) {
			renderer.processEntity(entity);
		}

	}

	/**
	 * Called every frame Put any renderer here
	 */
	private void render() {

		// // Water Setup
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		fbos.bindReflectionFrameBuffer();
		float distance = 2 * (activeCamera.getPosition().y - 0);
		activeCamera.getPosition().y -= distance;
		activeCamera.invertPitch();
		renderer.render(lights, activeCamera, false, new Vector4f(0, 1, 0, 0), sun, false);
		activeCamera.getPosition().y += distance;
		activeCamera.invertPitch();
		fbos.bindRefractionFrameBuffer();
		renderer.render(lights, activeCamera, false, new Vector4f(0, -1, 0, 0), sun, false);
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		fbos.unbindCurrentFrameBuffer();
		// //

		// / Main rendering
		renderer.render(lights, activeCamera, true, new Vector4f(0, 1, 0, 0), sun, true);

		// WaterRendering
		// waterRenderer.render(waters, activeCamera);

		//
		gui.render();

		// Test Font rendering

		for (IModule module : loadedModules) {
			module.render();
		}
	}

	private void cleanUp() {
		gui.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		fbos.cleanUp();

		for (IModule module : loadedModules) {
			module.cleanUp();
		}
		DisplayManager.closeDisplay();
		System.exit(0);
	}

}
