package newEntities;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.glEnable;

import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;

import newTerrains.Terrain;
import newUtility.Maths;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.Transform;
import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = -50;
	
	private Vector3f position = new Vector3f(100,100,0);	
	private float pitch = -10;
	private float yaw =0;
	private float roll;
	
	private Player player;
	
	
	
	public Camera(Player player){
		this.player= player;
		if (GLContext.getCapabilities().GL_ARB_depth_clamp) {
			glEnable(GL_DEPTH_CLAMP);
		}
	}
	
	public void move(Terrain terrain){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		
		Transform transform = player.getBody().getWorldTransform(new Transform());
		Quat4f rotation = new Quat4f();
		float rotationDegrees = Maths.convertToHeading(transform.getRotation(rotation));		
		calculateCameraPosition(horizontalDistance, verticalDistance, terrain, rotationDegrees, transform);			
		
		
		this.yaw = 180 - (angleAroundPlayer);
	}
	/*
	public void processMouse(float mouseSpeed, float maxLookUp,
			float maxLookDown) {

		float mouseDX =  Mouse.getDX() * mouseSpeed * 0.16f;

		float mouseDY =  Mouse.getDY() * mouseSpeed * 0.16f;

		if (yaw + mouseDX >= 360) {
			yaw = yaw + mouseDX - 360;

		} else if (yaw + mouseDX < 0) {
			yaw = 360 - yaw + mouseDX;

		} else {
			yaw += mouseDX;

		}
		if (pitch - mouseDY >= maxLookDown && pitch - mouseDY <= maxLookUp) {
			pitch += -mouseDY;

		} else if (pitch - mouseDY < maxLookDown) {
			pitch = maxLookDown;

		} else if (pitch - mouseDY > maxLookUp) {
			pitch = maxLookUp;
		}
	}
	*/
	public Vector3f getPosition() {
		return position;
	}
	
	public float getPitch() {
		return pitch;
	}
	public float getYaw() {
		return yaw;
	}
	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance, Terrain terrain, float rotationDegrees, Transform transform){

		//System.out.println(yRotDeg);
		float theta = (float) (angleAroundPlayer);
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		
		position.x = transform.origin.x - offsetX;
		position.z = transform.origin.z - offsetZ;
		position.y = transform.origin.y + verticalDistance;
		float terrainHeight = terrain.getHeightOfTerrain(position.x, position.z);

		if (position.y < terrainHeight + 2) {
		    position.y = terrainHeight + 2;

		}
	}
	
	


	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * 0.01f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * 0.2f;
			pitch -= pitchChange;
		}
	}
	
	
	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(1)){
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
}
