package main;

import static org.lwjgl.opengl.GL11.*;
import entities.Designer;
import graphics.ChunkBatch;
import graphics.EntityBatch;
import hud.HUDBuilder;

import java.io.IOException;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import threading.InterthreadHolder;
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

	private static int fps;
	private static int fpsCounter;
	private static long lastFPS;
	private ChunkManager chunkManager;
	private HUDBuilder hud;
	private long lastFrame;
	private boolean mouse0, mouse1;
	private long downStart;
	private int clickID;	
	

	// Render
	private void render() {
		// Clear the pixels on the screen and clear the contents of the depth
		// buffer (3D contents of the scene)
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Reset any translations the camera made last frame update
		glLoadIdentity();
		// Apply the camera position and orientation to the scene
		camera.applyTranslations();
		// glLight(GL_LIGHT0, GL_POSITION,
		// BufferTools.asFlippedFloatBuffer(500f, 100f, 500f, 1));
		// glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		// Render all chunks
		for (ChunkBatch cb : InterthreadHolder.getInstance().getChunkBatches()) {
			cb.draw(camera.x(), camera.y(), camera.z());
		}
		// Render all entities
		for (EntityBatch eb : InterthreadHolder.getInstance()
				.getEntityBatches()) {
			eb.draw(camera.x(), camera.y(), camera.z());
		}
		// System.out.print("FPS: " + fpsCounter);
		hud.render(fpsCounter, camera);
	}

	// Process Input
	private void input(float delta) {

		// We can only set the mouse to be grabbed once, if you set it again
		// every frame then nothing happens
		if (Mouse.isButtonDown(1) && !mouse1) {
			Mouse.setGrabbed(true);
			mouse1 = true;
		}
		if (Mouse.isButtonDown(0) && !mouse0) {
			mouse0 = true;
			downStart = getTime();
		}

		// Process Mouse events
		while (Mouse.next()) {
			if (Mouse.getEventButton() == 1) {
				if (!Mouse.getEventButtonState()) {
					Mouse.setGrabbed(false);
					mouse1 = false;
				}
			}
			// Get Clicks
			if (Mouse.getEventButton() == 0) {
				if (!Mouse.getEventButtonState()) {
					if(checkClick()){
						camera.moveFromLook(5, 5, 5);
					}
				}
			}		
			
			
		}

		if (Mouse.isGrabbed()) {
			camera.processMouse(1, 80, -80);
			// Designer.processMouse();
		}
		// System.out.println(camera.toString());
		camera.processKeyboard(delta, 10);

	}

	private boolean checkClick() {
		boolean click = false;
		if(mouse0){
			long downTime = getTime() - downStart;			
			if(downTime < 100){	
				click = true;			
			}
		}		
		mouse0 = false;
		return click;
	
	}

	private void cleanUp(boolean asCrash) {
		chunkManager.UnloadChunks();
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
		// glEnable(GL_LIGHTING);
		// glEnable(GL_LIGHT0);
		// glLightModel(GL_LIGHT_MODEL_AMBIENT,
		// BufferTools.asFlippedFloatBuffer(new float[] { 1, 1f, 1f, 1f }));
		// glLight(GL_LIGHT0, GL_CONSTANT_ATTENUATION,
		// BufferTools.asFlippedFloatBuffer(new float[] { 1, 1, 1, 1 }));

		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
		glMaterialf(GL_FRONT, GL_SHININESS, 50f);
		camera.applyOptimalStates();

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		glEnable(GL_TEXTURE_2D);

		glClearColor(0.2f, 0.2f, 0.2f, 0f);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);

		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

	}

	private void update(long delta) {
		Display.update();
		Display.sync(60);
	}

	private void enterGameLoop() {
		lastFPS = getTime();
		while (!Display.isCloseRequested()) {
			long delta = getDelta();
			render();
			input(delta);
			update(delta);
			updateFPS();
		}
	}

	/**
	 * Calculate how many milliseconds have passed since last frame.
	 * 
	 * @return milliseconds passed since last frame
	 */
	public long getDelta() {
		long time = getTime();
		long delta = (long) (time - lastFrame);
		lastFrame = time;
		// System.out.println(" Delta: " + delta);
		return delta;
	}

	private static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	private static void updateFPS() {
		if (getTime() - lastFPS > 1000) {

			// System.out.println("FPS: " + fps);
			fpsCounter = fps;
			fps = 0;
			lastFPS += 1000;
		}
		fps++;

	}

	private void setUpDisplay() {
		try {
			Display.setVSyncEnabled(true);
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
		// chunkManager.genTest(5, 5, 5, BlockType.BlockType_Dirt);

	}

	private void setUpHUD() {
		hud = new HUDBuilder();
		try {
			hud.setUpTextures();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		InterthreadHolder.getInstance();
		(new Thread(new OpenGLCamera())).start();
	}

}
