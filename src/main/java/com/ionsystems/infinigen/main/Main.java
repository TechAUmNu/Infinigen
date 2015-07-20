package main.java.com.ionsystems.infinigen.main;

import java.util.ArrayList;
import java.util.List;


























import java.util.Set;

import javax.sound.sampled.AudioInputStream;

import main.java.com.ionsystems.infinigen.cameras.RTSCamera;
import main.java.com.ionsystems.infinigen.cameras.ThirdPersonCamera;
import main.java.com.ionsystems.infinigen.entities.ICamera;
import main.java.com.ionsystems.infinigen.entities.Light;
import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.gui.GuiManager;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.networking.NetworkingManager;
import main.java.com.ionsystems.infinigen.objConverter.OBJFileLoader;
import main.java.com.ionsystems.infinigen.physics.PhysicsManager;
import main.java.com.ionsystems.infinigen.rendering.DisplayManager;
import main.java.com.ionsystems.infinigen.rendering.Loader;
import main.java.com.ionsystems.infinigen.rendering.MasterRenderer;
import main.java.com.ionsystems.infinigen.textures.ModelTexture;
import main.java.com.ionsystems.infinigen.unitBuilder.UnitBuilderManager;
import main.java.com.ionsystems.infinigen.utility.MousePicker;
import main.java.com.ionsystems.infinigen.utility.OSValidator;
import main.java.com.ionsystems.infinigen.world.ChunkManager;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

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
	private ICamera activeCamera;
	private int activeCameraID;
	private ThirdPersonCamera thirdPersonCamera;
	private RTSCamera rtsCamera;	
	private MasterRenderer renderer;
	private boolean mouse1 = false;

	private List<Light> lights;
	private UnitBuilderManager unitBuilder;
	private NetworkingManager networking;

	private List<IModule> loadedModules;
	private ChunkManager world;
	
	

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
			generateGui();
		}
		Globals.setLoading(false);

		if (!Globals.isServer()) {
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

			renderer = new MasterRenderer(loader);
			physics = new PhysicsManager();
			
			lights = new ArrayList<Light>();

			world = new ChunkManager();

			unitBuilder = new UnitBuilderManager();

			physics.setUp();
			Globals.setPhysics(physics);

			
			
			

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
			loadedModules.add(physics);

			

			// Cameras
			// loadedModules.add(thirdPersonCamera);
			loadedModules.add(rtsCamera);
			activeCameraID = 2;

			loadedModules.add(picker);
			loadedModules.add(unitBuilder);
			loadedModules.add(world);
			loadedModules.add(networking);

			// Add anything to the globals that might be needed elsewhere.
			Globals.setLoader(loader);
			loadAudio();

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
		//AudioManager.loadWAVAudioFile("stalker.wav");
		
		//AudioManager.playSoundEffect("stalker.wav", 1.0f, 1.0f, false, 100f, 100f,10f);
		
		
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
		
	}

	/**
	 * Put anything to do with loading models etc here
	 */
	private void loadAssets() {
		lights.add(new Light(new Vector3f(0, 10000, -9000), new Vector3f(1, 1, 1))); // Sun

	}




	/**
	 * Anything to do with setting up the gui
	 */
	private void generateGui() {
		//gui.generateElement(0, 0, "uvgrid01");
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

		renderer.render(lights, activeCamera);
		gui.render();

		// for (IModule module : loadedModules) {
		// / module.render();
		// }
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
