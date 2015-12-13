package main.java.com.ionsystems.infinigen.cameras;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.rendering.DisplayManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class RTSCamera implements IModule, ICamera {

	private float scrollSpeed = 50;
	private Vector3d direction = new Vector3d();;

	private Vector3f position = new Vector3f(0, 20, 0);
	private float pitch = -10;
	private float yaw = -90;

	private Vector3d calculateDirectionVector() {

		direction.x = cos(Math.toRadians(yaw)) * cos(Math.toRadians(pitch));
		direction.y = sin(Math.toRadians(yaw)) * cos(Math.toRadians(pitch));
		direction.z = sin(Math.toRadians(pitch));

		return direction;
	}

	public RTSCamera() {
	}

	@Override
	public void update() {
		calculateZoom();
		calculatePitch();
		calculateYaw();

		calculatePosition();
		Globals.setCameraDirection(calculateDirectionVector());

		float terrainHeight = -1000;

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
		float dx = 0, dz = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			dx = scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			dx = -scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			dz = -scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			dz = scrollSpeed * DisplayManager.getFrameTimeSeconds();
		}

		position.z += dx * (float) cos(toRadians(yaw - 90)) + dz * cos(toRadians(yaw));
		position.x -= dx * (float) sin(toRadians(yaw - 90)) + dz * sin(toRadians(yaw));

	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
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
			if (pitch > 90) {
				pitch = 90;
			}
			if (pitch < -25) {
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

	@Override
	public void invertPitch() {
		this.pitch = -pitch;

	}

}
