package newRendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	private static final int WIDTH = 1024;
	private static final int HEIGHT = 1024;
	private static final int FPS_CAP = 240;
	private static float GUI_SCALE = 1f;
	private static float GUI_SCALE_FACTOR = 0.5f;

	private static long lastFrameTime;
	private static float delta;
	private static int fps;
	private static float lastFPS;
	private static int fpsCounter;

	public static void createDisplay() {

		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);

		try {
			Display.setFullscreen(false);
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(8, 24, 0, 8), attribs);
			Display.setTitle("New Features testing");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		lastFPS = getCurrentTime();
		calcScale();
	}

	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		// System.out.println(delta);
		lastFrameTime = currentFrameTime;
		calculateFPS();
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	private static void calculateFPS() {

		if (getCurrentTime() - lastFPS > 1000f) {

			System.out.println("FPS: " + fps);
			fpsCounter = fps;
			fps = 0;
			lastFPS += 1000f;
		}
		fps++;

		// System.out.println(fpsCounter);

	}

	public static void closeDisplay() {
		Display.destroy();
	}

	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static float getGUIScale() {
		return GUI_SCALE;
	}

	public static void calcScale() {

		GUI_SCALE = (float) (Display.getWidth() / 1920f) * GUI_SCALE_FACTOR;
	}

	public static int getFpsCounter() {
		return fpsCounter;
	}

}
