package main;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import entities.Designer;
import graphics.ChunkBatch;
import graphics.EntityBatch;
import hud.HUDBuilder;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBDepthClamp;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

import threading.InterthreadHolder;
import utility.BufferTools;
import utility.EulerCamera;
import world.ChunkManager;

public class OpenGLCamera implements Runnable {
	private static final String WINDOW_TITLE = "Infinigen";
	private static final int[] WINDOW_DIMENSIONS = { 1920, 1080 };
	private static final float ASPECT_RATIO = (float) WINDOW_DIMENSIONS[0]
			/ (float) WINDOW_DIMENSIONS[1];

	private static final EulerCamera camera = new EulerCamera.Builder()
			.setPosition(0f, 0f, 0f).setRotation(50, 12, 0)
			.setAspectRatio(ASPECT_RATIO).setFieldOfView(60)
			.setFarClippingPane(10000f).setNearClippingPane(0.1f).build();

	/**
	 * The shader program that will use the lookup texture and the height-map's
	 * vertex data to draw the terrain.
	 */
	private static int shaderProgram;
	/**
	 * The texture that will be used to find out which colours correspond to
	 * which heights.
	 */

	/** The display list that will contain the height-map's vertex data. */
	private static int heightmapDisplayList;
	private static int fps;
	private static int fpsCounter;
	private static long lastFPS;
	private ChunkManager chunkManager;
	private HUDBuilder hud;
	private float lastFrame;

	// Render
	private void render() {
		// Clear the pixels on the screen and clear the contents of the depth
		// buffer (3D contents of the scene)
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Reset any translations the camera made last frame update
		glLoadIdentity();
		// Apply the camera position and orientation to the scene
		camera.applyTranslations();
		glLight(GL_LIGHT0, GL_POSITION,
				BufferTools.asFlippedFloatBuffer(500f, 100f, 500f, 1));
		// glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		//Render all chunks
		for (ChunkBatch cb : InterthreadHolder.getInstance().getChunkBatches()) {
			cb.draw(camera.x(), camera.y(), camera.z());
		}
		//Render all entities
		for(EntityBatch eb : InterthreadHolder.getInstance().getEntityBatches()){
			eb.draw(camera.x(), camera.y(), camera.z());
		}
		System.out.print("FPS: " + fpsCounter);
		hud.render(fpsCounter);
	}

	// Process Input
	private void input(float delta) {
		if (Mouse.isButtonDown(0)) {
			Mouse.setGrabbed(true);
		} else if (Mouse.isButtonDown(1)) {
			Mouse.setGrabbed(false);
		}
		if (Mouse.isGrabbed()) {
			camera.processMouse(1, 80, -80);
			Designer.processMouse();
		}
		// System.out.println(camera.toString());
		camera.processKeyboard(delta, 10);
		
	}

	private void cleanUp(boolean asCrash) {

		System.err.println(GLU.gluErrorString(glGetError()));
		Display.destroy();
		System.exit(asCrash ? 1 : 0);
	}

	private void setUpMatrices() {
		camera.applyPerspectiveMatrix();
	}

	private void setUpStates() {
		glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glLightModel(GL_LIGHT_MODEL_AMBIENT,
				BufferTools.asFlippedFloatBuffer(new float[] { 0, 0f, 0f, 1f }));
		glLight(GL_LIGHT0, GL_CONSTANT_ATTENUATION,
				BufferTools.asFlippedFloatBuffer(new float[] { 1, 1, 1, 1 }));

		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
		glMaterialf(GL_FRONT, GL_SHININESS, 50f);
		camera.applyOptimalStates();

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		glEnable(GL_TEXTURE_2D);

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);

		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
		
	}

	private void update(float delta) {
		Display.update();
		Display.sync(60);
	}

	private void enterGameLoop() {
		lastFPS = getTime();
		while (!Display.isCloseRequested()) {
			float delta = getDelta();
			render();
			input(delta);
			update(delta);
			updateFPS();
		}
	}
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public float getDelta() {
	    float time = getTime();
	    float delta = (float) (time - lastFrame);
	    lastFrame = time;
	    System.out.println(" Delta: " + delta);
	    return delta;
	}
	private static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private static void updateFPS() {
		if (getTime() - lastFPS > 1000) {

			//System.out.println("FPS: " + fps);
			fpsCounter = fps;
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
		
	}

	private void setUpDisplay() {
		try {
			Display.setVSyncEnabled(false);
			Display.setFullscreen(true);
			Display.setResizable(false);
			Display.setTitle(WINDOW_TITLE);
			Display.create(new PixelFormat(4, 24, 0, 4));

		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp(true);
		}
	}

	@Override
	public void run() {

		setUpDisplay();
		setUpStates();
		setUpChunks();
		Designer d = new Designer();
		d.initDesigner(new ChunkManager(), camera);
		setUpHUD();
		setUpMatrices();

		enterGameLoop();
		cleanUp(false);

	}

	private void setUpChunks() {
		chunkManager = new ChunkManager();
		chunkManager.genTest(1, 1, 1);

	}

	private void setUpHUD() {
		hud = new HUDBuilder();
	}

	public static void main(String[] args) {
		InterthreadHolder.getInstance();
		(new Thread(new OpenGLCamera())).start();
	}

}
