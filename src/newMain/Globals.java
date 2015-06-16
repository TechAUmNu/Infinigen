package newMain;

import org.lwjgl.util.vector.Vector3f;

import newRendering.Loader;

public class Globals {

	private static float gravity = 9.81f;
	private static Loader loader;
	private static Vector3f mouseRay;
	private static Vector3f cameraPosition;

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
		mouseRay = ray;
	}
	
	public static Vector3f getMouseRay(){
		return mouseRay;
	}
	
	public static Vector3f getMousePhysicsRay(){
		
		Vector3f ray = mouseRay;
		ray.scale(1000);
		return ray;
	}

	public static void setCameraPosition(Vector3f position) {
		cameraPosition = position;		
	}
	
	public static Vector3f getCameraPosition(){
		return cameraPosition;
	}
}
