package newCameras;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;

import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;

import newEntities.ICamera;
import newEntities.PhysicsEntity;
import newMain.Globals;
import newMain.IModule;
import newRendering.DisplayManager;
import newTerrains.Terrain;
import newUtility.Maths;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.Transform;
import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

public class RTSCamera implements IModule, ICamera {

	private float scrollSpeed = 10;
	

	

	private Vector3f position = new Vector3f(0, 50, 0);
	private float pitch = -10;
	private float yaw = 0;	

	

	public RTSCamera() {
	}

	public void update() {
		calculateZoom();
		calculatePitch();
		calculateYaw();
		
		calculatePosition();
		
		
		float terrainHeight = (float) 0;

		if (position.y < terrainHeight) {
			position.y = terrainHeight;

		}
		
		

		
		
		Globals.setCameraPosition(position);
	}

	private void calculateYaw() {
		if (Mouse.isButtonDown(1)) {
			float yawChange = Mouse.getDX() * 0.2f;
			
			
			yaw += yawChange;
					
		}
		
	}

	private void calculatePosition() {
		float dx = 0,dz = 0;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			dx = scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			dx = -scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			dz = -scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			dz = scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}	
		
		position.z += dx * (float) cos(toRadians(yaw - 90)) + dz * cos(toRadians(yaw));
		position.x -= dx * (float) sin(toRadians(yaw - 90)) + dz * sin(toRadians(yaw));
		
		
		
	}

	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	
	
		
		


	

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.01f;
		position.y -= zoomLevel;
	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.2f;
			pitch -= pitchChange;
			if(pitch > 90){
				pitch = 90;
			}
			if(pitch < -25){
				pitch = -25;
			}
			
		}
	}

	

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}

}
