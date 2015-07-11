package newMain;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import newRendering.Loader;
import newWorld.Chunk;

public class Globals {

	private static float gravity = 9.81f;
	private static Loader loader;
	private static Vector3f mouseRay;
	private static Vector3f cameraPosition;
	private static ArrayList<Chunk> visibleChunks;
	private static boolean isServer;
	

	// //////////////////////////////////////////////////////////////////////////////////////

	public static float getGravity() {
		return gravity;
	}

	public static void setGravity(float gravity) {
		Globals.gravity = gravity;
	}

	public static Loader getLoader() {
		return loader;
	}

	public static void setLoader(Loader loader) {
		Globals.loader = loader;
	}
	
	public static void setCurrentMouseRay(Vector3f ray){
		Globals.mouseRay = ray;
	}
	
	public static Vector3f getMouseRay(){
		return mouseRay;
	}
	
	

	public static boolean isServer() {
		return isServer;
	}

	public static void setServer(boolean isServer) {
		Globals.isServer = isServer;
	}

	public static void setVisibleChunks(ArrayList<Chunk> visibleChunks) {
		Globals.visibleChunks = visibleChunks;
	}

	public static ArrayList<Chunk> getVisibleChunks(){
		return visibleChunks;
	}

	public static void setCameraPosition(Vector3f position) {
		Globals.cameraPosition = position;		
	}
	
	public static Vector3f getCameraPosition(){
		return cameraPosition;
	}
}
