package main.java.com.ionsystems.infinigen.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.vecmath.Vector3d;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.RigidBody;

import main.java.com.ionsystems.infinigen.cameras.ICamera;
import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.networking.ChunkData;
import main.java.com.ionsystems.infinigen.physics.DebugDrawer;
import main.java.com.ionsystems.infinigen.physics.PhysicsManager;
import main.java.com.ionsystems.infinigen.rendering.Loader;
import main.java.com.ionsystems.infinigen.rendering.MasterRenderer;
import main.java.com.ionsystems.infinigen.textures.ModelTexture;
import main.java.com.ionsystems.infinigen.world.Chunk;


public class Globals {

	private static float gravity = 9.81f;
	private static Loader loader;
	private static Vector3f mouseRay;
	private static Vector3f cameraPosition;
	private static CopyOnWriteArrayList<Chunk> visibleChunks;
	private static boolean isServer;
	private static boolean loading;
	
	
	private static ArrayList<ChunkData> chunkUpdate;
	private static String ip;
	private static int port;
	private static ArrayList<RigidBody> bodies;
	private static PhysicsManager physics;
	private static ArrayList<PhysicsEntity> newEntities = new ArrayList<PhysicsEntity>();
	private static ArrayList<PhysicsEntity> entities = new ArrayList<PhysicsEntity>();
	
	private static HashMap<String, PhysicsModel> loadedPhysicsModels = new HashMap<String, PhysicsModel>();
	
	private static int rigidBodyID = 35;
	private static int clientID = 0;
	
	private static PhysicsEntity cameraEntity;
	private static Vector3d cameraDirection;
	private static int activeCameraID;
	private static boolean debugRendering;
	private static MasterRenderer renderer;
	private static ICamera activeCamera;
	
	
//	private static ArrayList<TextElement> textElements = new ArrayList<TextElement>();
	
	private static float placementOffset;
	private static boolean running = true;
	

	// //////////////////////////////////////////////////////////////////////////////////////

	public static Vector3d getCameraDirection() {
		return cameraDirection;
	}

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

	public static void setLoadedChunks(CopyOnWriteArrayList<Chunk> visibleChunks) {
		Globals.visibleChunks = visibleChunks;
	}

	public static CopyOnWriteArrayList<Chunk> getLoadedChunks(){
		return visibleChunks;
	}

	public static void setCameraPosition(Vector3f position) {
		Globals.cameraPosition = position;		
	}
	
	public static Vector3f getCameraPosition(){
		return cameraPosition;
	}

	public static boolean showFPS() {
		
		return true;
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

	public static void setCameraDirection(Vector3d direction) {
		Globals.cameraDirection = direction;		
	}

	public static void setActiveCameraID(int i) {
		Globals.activeCameraID = i;		
	}

	public static int getActiveCameraID() {
		return activeCameraID;
	}

	public static boolean debugRendering() {
		return debugRendering;
	}

	public static void setDebugRendering(boolean debugRendering) {
		Globals.debugRendering = debugRendering;
		
		if(debugRendering){
			if(Globals.getPhysics().getProcessor().getDynamicsWorld().getDebugDrawer() == null){
				DebugDrawer drawer = new DebugDrawer();
				Globals.getPhysics().getProcessor().getDynamicsWorld().setDebugDrawer(drawer);	
			}
		}
	}

	public static void setRenderer(MasterRenderer renderer) {
		Globals.renderer = renderer;		
	}

	public static MasterRenderer getRenderer() {
		return renderer;
	}

	public static void setActiveCamera(ICamera activeCamera) {
		Globals.activeCamera = activeCamera;
		
	}

	public static ICamera getActiveCamera() {
		return activeCamera;
	}
	
	public static void setPlacementOffset(float placementOffset) {
		Globals.placementOffset = placementOffset;
		
	}

	public static float getPlacementOffset() {
		return placementOffset;
	}
	
	public static boolean isModelLoaded(String name) {
		return loadedPhysicsModels.containsKey(name);
	}
	
	public static PhysicsModel getModel(String name){
		return loadedPhysicsModels.get(name);
	}

	public static void addLoadedPhysicsModel(String name, PhysicsModel model) {
		Globals.loadedPhysicsModels.put(name, model);
	}

	public static TexturedPhysicsModel getModelWithTexture(String model, String texture) {
		return new TexturedPhysicsModel(Globals.getModel(model), new ModelTexture(Globals.getLoader().loadTexture(texture)));
		
	}
	
	public static TexturedPhysicsModel getTexturedModel(String model) {
		return new TexturedPhysicsModel(Globals.getModel(model), new ModelTexture(Globals.getLoader().loadTexture(Globals.getModel(model).getTexture())));		
	}
	
	public static TexturedPhysicsModel getTexturedModel(PhysicsModel model){
		return new TexturedPhysicsModel(model, new ModelTexture(Globals.getLoader().loadTexture(model.getTexture())));
	}

	public static boolean isRunning() {		
		return running;
	}
	
	public static void endThreads(){
		running = false;
	}

//	public static ArrayList<TextElement> getTextElements() {
//		return textElements;
//	}
//
//	public static void setTextElements(ArrayList<TextElement> textElements) {
//		Globals.textElements = textElements;
//	}
//	
//	public static void addTextElement(TextElement textElement){
//		Globals.textElements.add(textElement);
//	}
	
	
}
