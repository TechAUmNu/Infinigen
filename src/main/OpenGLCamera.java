package main;

import static org.lwjgl.opengl.GL11.*;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import entities.Designer;
import graphics.ChunkBatch;
import graphics.EntityBatch;
import hud.HUDBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import threading.InterthreadHolder;
import utility.BufferTools;
import utility.EulerCamera;
import world.Block.BlockType;
import world.ChunkManager;

public class OpenGLCamera implements Runnable {
	private static final String WINDOW_TITLE = "Infinigen Tech Demo";
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
	private HUDBuilder hud;
	private long lastFrame;
	private boolean mouse0, mouse1;
	private long downStart;
	
	private Texture textureHandle;
	private int i;

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
		BufferTools.asFlippedFloatBuffer(35f, 35f, 35f, 1));
		// glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		// Render all chunks
		for (ChunkBatch cb : InterthreadHolder.getInstance().getChunkBatches()) {
			cb.draw(camera.x(), camera.y(), camera.z(), textureHandle);
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
		while(Keyboard.next()){
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
				cleanUp(false);
			}
		}
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
		ChunkManager.getInstance().UnloadChunks();
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
		 BufferTools.asFlippedFloatBuffer(new float[] { 0.01f, 0.01f, 0.01f, 0.01f }));
		 glLight(GL_LIGHT0, GL_CONSTANT_ATTENUATION,
		 BufferTools.asFlippedFloatBuffer(new float[] { 1, 1, 1, 1 }));

		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
		glMaterialf(GL_FRONT, GL_SHININESS, 50f);
		camera.applyOptimalStates();
		
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);


		glClearColor(0.2f, 0.2f, 0.2f, 0f);
		

		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

	}

	private void update(long delta) {
	
		//ChunkManager.getInstance().update();
		Display.update();
		Display.sync(120);
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
			//Display.setDisplayMode(new DisplayMode(1280,720));
			Display.setVSyncEnabled(false);
			Display.setFullscreen(false);
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
		//Designer d = new Designer();
		//d.initDesigner(camera);
		setUpHUD();
		setUpMatrices();
		setUpTextures();
		enterGameLoop();
		cleanUp(false);

	}

	private void setUpTextures() {		
		
				
					try {
						textureHandle = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/images/grass.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
	}
	
	
	 private int loadPNGTexture(String filename, int textureUnit) {
	        ByteBuffer buf = null;
	        int tWidth = 0;
	        int tHeight = 0;
	         
	        try {
	            // Open the PNG file as an InputStream
	            InputStream in = new FileInputStream(filename);
	            // Link the PNG decoder to this stream
	            PNGDecoder decoder = new PNGDecoder(in);
	             
	            // Get the width and height of the texture
	            tWidth = decoder.getWidth();
	            tHeight = decoder.getHeight();
	             
	             
	            // Decode the PNG file in a ByteBuffer
	            buf = ByteBuffer.allocateDirect(
	                    4 * decoder.getWidth() * decoder.getHeight());
	            decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
	            buf.flip();
	             
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.exit(-1);
	        }
	         
	        // Create a new texture object in memory and bind it
	        int texId = GL11.glGenTextures();
	        GL13.glActiveTexture(textureUnit);
	        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
	         
	        // All RGB bytes are aligned to each other and each component is 1 byte
	        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
	         
	        // Upload the texture data and generate mip maps (for scaling)
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0, 
	                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
	        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
	         
	        // Setup the ST coordinate system
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	         
	        // Setup what to do when the texture has to be scaled
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
	                GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
	                GL11.GL_LINEAR_MIPMAP_LINEAR);
	         
	        return texId;
	    }

	private void setUpChunks() {		
		 ChunkManager.getInstance().genTest(5, 1, 5, BlockType.BlockType_Dirt);

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
		(new Thread(new OpenGLCamera())).start();
	}

}
