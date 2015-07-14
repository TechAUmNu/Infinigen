package newMain;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.RigidBody;

import newEntities.PhysicsEntity;
import newNetworking.ChunkData;
import newRendering.Loader;
import newWorld.Chunk;


public class Globals {

	private static float gravity = 9.81f;
	private static Loader loader;
	private static Vector3f mouseRay;
	private static Vector3f cameraPosition;
	private static ArrayList<Chunk> visibleChunks;
	private static boolean isServer;
	private static boolean loading;
	
	private static ArrayList<ChunkData> chunkUpdate;
	private static String ip;
	private static int port;
	private static ArrayList<RigidBody> bodies;
	

	// //////////////////////////////////////////////////////////////////////////////////////

	public static ArrayList<RigidBody> getBodies() {
		return bodies;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Globals.port = port;
	}

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		Globals.ip = ip;
	}

	public static ArrayList<ChunkData> getChunkUpdate() {
		if(chunkUpdate == null){
			chunkUpdate = new ArrayList<ChunkData>();
		}
		return chunkUpdate;
	}

	public static void setChunkUpdate(ArrayList<ChunkData> chunkUpdate) {
		Globals.chunkUpdate = chunkUpdate;
	}

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
	
	public static void setLoading(boolean loading){
		Globals.loading = loading;
	}
	

	public static boolean isServer() {
		return isServer;
	}

	public static void setServer(boolean isServer) {
		Globals.isServer = isServer;
	}

	public static void setLoadedChunks(ArrayList<Chunk> visibleChunks) {
		Globals.visibleChunks = visibleChunks;
	}

	public static ArrayList<Chunk> getLoadedChunks(){
		return visibleChunks;
	}

	public static void setCameraPosition(Vector3f position) {
		Globals.cameraPosition = position;		
	}
	
	public static Vector3f getCameraPosition(){
		return cameraPosition;
	}

	public static boolean showFPS() {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean loading() {		
		return loading;
	}

	public static void setBodies(ArrayList<RigidBody> bodies) {
		Globals.bodies = bodies;		
	}

	
}
