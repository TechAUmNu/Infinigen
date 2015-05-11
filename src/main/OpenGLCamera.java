package main;




import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;


import graphics.ChunkBatch;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import physics.PhysicsManager;
import threading.DataStore;
import utility.BufferTools;
import utility.EulerCamera;
import world.Block.BlockType;
import world.ChunkManager;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;


public class OpenGLCamera implements Runnable {
	private static final String WINDOW_TITLE = "Infinigen Tech Demo";
	private static final int[] WINDOW_DIMENSIONS = { 1920, 1080 };
	private static final float ASPECT_RATIO = (float) WINDOW_DIMENSIONS[0]
			/ (float) WINDOW_DIMENSIONS[1];

	private static final EulerCamera camera = new EulerCamera.Builder()
			.setPosition(140f, 100000f, 140f).setRotation(50, 320, 0)
			.setAspectRatio(ASPECT_RATIO).setFieldOfView(60)
			.setFarClippingPane(10000f).setNearClippingPane(0.1f).build();

	private static int fps;
	private static int fpsCounter;
	private static long lastFPS;	
	//private HUDBuilder hud;
	private double lastFrame;
	private boolean mouse0, mouse1;
	private long downStart;

	
	private Texture textureHandle;
	private boolean createNewShape;
	private int id;
	
	// Render
	private void render() {
		System.out.println("----------------render----------------");
		// Clear the pixels on the screen and clear the contents of the depth
		// buffer (3D contents of the scene)
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Reset any translations the camera made last frame update
		glLoadIdentity();
		// Apply the camera position and orientation to the scene
		camera.applyTranslations();
		 glLight(GL_LIGHT0, GL_POSITION,
		BufferTools.asFlippedFloatBuffer(35f, 100f, 35f, 1));
		// glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		// Render all chunks
		for (ChunkBatch cb : DataStore.getInstance().getChunkBatches()) {
			cb.draw(camera.x(), camera.y(), camera.z(), textureHandle);
		}
		
		//EntityManager.getInstance().drawAll();
		
		// System.out.print("FPS: " + fpsCounter);
		//hud.render(fpsCounter, camera);
	}

	// Process Input
	private void input(float delta) {
		System.out.println("----------------input----------------");
		
		
		while(Keyboard.next()){
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
				cleanUp(false);
			}
			
			if(Keyboard.getEventKey() == Keyboard.KEY_G){
				createNewShape = true;
				
			}else{
				//createNewShape = false;
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
		System.out.println("----------------Cleanup----------------");
		ChunkManager.getInstance().UnloadChunks();
		//EntityManager.getInstance().cleanUp();
		PhysicsManager.getInstance().cleanUp();
		System.err.println(GLU.gluErrorString(glGetError()));
		Display.destroy();
		System.exit(asCrash ? 1 : 0);
	}

	private void setUpMatrices() {
		camera.applyPerspectiveMatrix();
	}

	private void setUpStates() {
		System.out.println("----------------Set up States----------------");
		glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		//glDepthFunc(GL_LEQUAL);
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


		glClearColor(0.529f, 0.8078f, 0.980f, 0f);
		

		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
	}

	private void logic(float delta) {		
		System.out.println("----------------Logic----------------");
		// Reset the model-view matrix.
        glLoadIdentity();
        // Apply the camera's position and orientation to the model-view matrix.
        camera.applyTranslations();
        // Runs the JBullet physics simulation for the specified time in seconds.
        //PhysicsManager.getInstance().stepSimulate(delta);
        // Create a set of bodies that are to be removed.
        Set<RigidBody> bodiesToBeRemoved = new HashSet<RigidBody>();
        // For every physics ball ...
        //EntityManager.getInstance().process(delta);
        
        if (createNewShape) {
            // Create the collision shape (sphere with radius of 3 metres).
            CollisionShape shape = new BoxShape(new Vector3f(1f,1f,1f));
            // Create the motion state (x and z are the same as the camera's).
            DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(100, 100, 100), 1.0f)));
            // Calculate the inertia (resistance to movement) using the ball's mass of 1 kilogram.
            Vector3f inertia = new Vector3f(0, 0, 0);
            shape.calculateLocalInertia(1.0f, inertia);
            RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(1, motionState, shape, inertia);
            constructionInfo.restitution = 0.75f;
            RigidBody rigidBody = new RigidBody(constructionInfo);
            //EntityManager.getInstance().addEntity(rigidBody, id);
            id++;
            if(id >= DataStore.getInstance().calcNumberCores()){
            	id = 0;
            }
           
        }        
	}
	
	private void update(float delta) {	
		System.out.println("----------------update----------------");
		Display.update();
		//Display.sync(120);
		//createNewShape = true;
	}

	private void enterGameLoop() {
		lastFPS = getTime();
		while (!Display.isCloseRequested()) {
			float delta = getDelta();
			if(delta <= 0){delta = 1;}
			DataStore.getInstance().setDelta(delta);
			render();
			input(delta);
			logic(delta);
			update(delta);
			updateFPS();
		}
	}

	

	/**
	 * Calculate how many milliseconds have passed since last frame.
	 * 
	 * @return milliseconds passed since last frame
	 */
	public float getDelta() {
		float time = getTime();
		float delta = (float) (time - lastFrame);
		lastFrame = time;
		//System.out.println(" Delta: " + delta);
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
			textureHandle = TextureLoader.getTexture("PNG",
					ResourceLoader.getResourceAsStream("res/textures/grassy.png"));
			
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.3f);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void setUpChunks() {		
		 ChunkManager.getInstance().genTest(10, 1, 10, BlockType.BlockType_Dirt);
	}

	private void setUpHUD() {
		//hud = new HUDBuilder();
		//try {
		//	hud.setUpTextures();
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
	}

	public static void main(String[] args) {	
		System.setProperty("org.lwjgl.librarypath", new File("natives/windows").getAbsolutePath());
		(new Thread(new OpenGLCamera())).start();
	}

}
