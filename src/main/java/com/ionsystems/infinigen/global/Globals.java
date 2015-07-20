package main.java.com.ionsystems.infinigen.global;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.RigidBody;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.physics.PhysicsManager;
import main.java.com.ionsystems.infinigen.physics.PhysicsProcessor;
import main.java.com.ionsystems.infinigen.rendering.Loader;
import main.java.com.ionsystems.infinigen.world.Chunk;


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
	private static PhysicsManager physics;
	private static ArrayList<PhysicsEntity> newEntities = new ArrayList<PhysicsEntity>();
	private static ArrayList<PhysicsEntity> entities = new ArrayList<PhysicsEntity>();
	private static int rigidBodyID = 35;
	private static int clientID = 0;
	
	private static PhysicsEntity cameraEntity;
	
	
	
	

	// //////////////////////////////////////////////////////////////////////////////////////

	public static void setClientID(int clientID) {
		Globals.clientID = clientID;
	}

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

	public static PhysicsManager getPhysics() {
		return physics;
	}

	public static void setPhysics(PhysicsManager physics) {
		Globals.physics = physics;
	}

	public static ArrayList<PhysicsEntity> getNewEntities() {	//This must clear the list otherwise we will add the same thing a lot!		
		ArrayList<PhysicsEntity> ne = new ArrayList<PhysicsEntity>(); //TODO: maybe put a lock on newEntities here?
		ne.addAll(newEntities);
		newEntities.clear();
		return ne;
	}

	public static void addEntity(PhysicsEntity newEntity, boolean fromNetwork) {
		if(!fromNetwork){
			Globals.newEntities.add(newEntity);
		}		
		entities.add(newEntity);
	}

	public static ArrayList<PhysicsEntity> getEntities() {
		return entities;
	}

	public static void setEntities(ArrayList<PhysicsEntity> entities) { //For network download
		Globals.entities = entities;
	}

	public static int getRigidBodyID() {
		return rigidBodyID++;
	}

	public static int getClientID() {
		if(Globals.isServer){
			return clientID++;
		}
		return clientID;
	}

	public static PhysicsEntity getCameraEntity() {
		return cameraEntity;
	}

	public static void setCameraEntity(PhysicsEntity cameraEntity) {
		Globals.cameraEntity = cameraEntity;
	}
	

	
	
}
