package main.java.com.ionsystems.infinigen.entities;

import org.lwjgl.util.vector.Vector3f;

public interface ICamera {

	/*
	 * public void processMouse(float mouseSpeed, float maxLookUp, float
	 * maxLookDown) {
	 * 
	 * float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
	 * 
	 * float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
	 * 
	 * if (yaw + mouseDX >= 360) { yaw = yaw + mouseDX - 360;
	 * 
	 * } else if (yaw + mouseDX < 0) { yaw = 360 - yaw + mouseDX;
	 * 
	 * } else { yaw += mouseDX;
	 * 
	 * } if (pitch - mouseDY >= maxLookDown && pitch - mouseDY <= maxLookUp) {
	 * pitch += -mouseDY;
	 * 
	 * } else if (pitch - mouseDY < maxLookDown) { pitch = maxLookDown;
	 * 
	 * } else if (pitch - mouseDY > maxLookUp) { pitch = maxLookUp; } }
	 */
	public abstract Vector3f getPosition();

	public abstract float getPitch();

	public abstract float getYaw();

}