package main.java.com.ionsystems.infinigen.cameras;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;

import javax.vecmath.Quat4f;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.utility.Maths;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.Transform;

public class ThirdPersonCamera implements IModule, ICamera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = -50;

	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = -10;
	private float yaw = 0;
	private float roll;

	private PhysicsEntity entity;
	private boolean hasEntity = false;

	public ThirdPersonCamera() {		
		if (GLContext.getCapabilities().GL_ARB_depth_clamp) {
			glEnable(GL_DEPTH_CLAMP);
		}
	}
	
	public void setEntity(PhysicsEntity entity){
		this.entity = entity;
		hasEntity = true;
	}

	public void update() {
		if(Globals.getCameraEntity() != null){
			setEntity(Globals.getCameraEntity());
		}
		if(hasEntity){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();

		Transform transform = entity.getBody().getWorldTransform(new Transform());
		Quat4f rotation = new Quat4f();
		float rotationDegrees = Maths.convertToHeading(transform.getRotation(rotation));
		calculateCameraPosition(horizontalDistance, verticalDistance, rotationDegrees, transform);

		this.yaw = 180 - (angleAroundPlayer);
		
		Globals.setCameraPosition(position);
		}
	}

	
	@Override
	public Vector3f getPosition() {
		return position;
	}

	/* (non-Javadoc)
	 * @see newEntities.ICamera#getPitch()
	 */
	@Override
	public float getPitch() {
		return pitch;
	}

	/* (non-Javadoc)
	 * @see newEntities.ICamera#getYaw()
	 */
	@Override
	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance, float rotationDegrees, Transform transform) {

		// System.out.println(yRotDeg);
		float theta = (float) (angleAroundPlayer);
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));

		position.x = transform.origin.x - offsetX;
		position.z = transform.origin.z - offsetZ;
		position.y = transform.origin.y + verticalDistance;
		float terrainHeight = (float) -1;

		if (position.y < terrainHeight) {
			position.y = terrainHeight;

		}
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.01f;
		distanceFromPlayer -= zoomLevel;
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

	private void calculateAngleAroundPlayer() {
		if (Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
			
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
		return new ArrayList<PhysicsEntity>();
	}
	
	@Override
	public void invertPitch() {
		this.pitch = -pitch;
		
	}

}
