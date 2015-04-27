package newEntities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

public class Camera {
	
	private Vector3f position = new Vector3f(0,0,0);	
	private float pitch;
	private float yaw;
	private float roll;
	
	
	public void move(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			position.z -= 0.16f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			position.x += 0.16f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			position.x -= 0.16f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			position.y += 0.16f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
			position.y -= 0.16f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			position.z += 0.16f;
		}
	}
	
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
	
	
}
