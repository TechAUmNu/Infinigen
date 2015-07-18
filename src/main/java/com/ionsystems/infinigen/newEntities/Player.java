package main.java.com.ionsystems.infinigen.newEntities;

import java.util.ArrayList;

import javax.vecmath.Quat4f;

import main.java.com.ionsystems.infinigen.newMain.IModule;
import main.java.com.ionsystems.infinigen.newModels.TexturedModel;
import main.java.com.ionsystems.infinigen.newModels.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.newPhysics.PhysicsProcessor;
import main.java.com.ionsystems.infinigen.newRendering.DisplayManager;
import main.java.com.ionsystems.infinigen.newUtility.Maths;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class Player extends PhysicsEntity implements IModule {

	private static final float RUN_SPEED = 600;
	private static final float TURN_SPEED = 100;
	private static final float GRAVITY = -10000;
	private static final float JUMP_POWER = 10000;
	private static final float TERRAIN_HEIGHT = 0;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private boolean isInAir = false;

	public Player(TexturedPhysicsModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, PhysicsProcessor processor) {
		super(model, position, rotX, rotY, rotZ, scale, scale, processor);
		super.body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
	}

	public void update() {
		super.body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
		checkInputs();
		// super.body.setFriction(5);
		super.body.applyTorque(new javax.vecmath.Vector3f(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0));

		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		Quat4f rotation = super.body.getWorldTransform(new Transform()).getRotation(new Quat4f());

		float yRotation = Maths.convertToHeading(rotation);
		float dx = (float) (distance * Math.sin(Math.toRadians(yRotation)));
		float dz = (float) (distance * Math.cos(Math.toRadians(yRotation)));

		super.body.applyForce(new javax.vecmath.Vector3f(dx, 0, dz), new javax.vecmath.Vector3f(dx, 0, dz));

		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		// super.increasePosition(0, upwardsSpeed *
		// DisplayManager.getFrameTimeSeconds(), 0);
		// super.body.applyForce(new javax.vecmath.Vector3f(0, upwardsSpeed *
		// DisplayManager.getFrameTimeSeconds(), 0), new
		// javax.vecmath.Vector3f(0, upwardsSpeed *
		// DisplayManager.getFrameTimeSeconds(), 0));
		float terrainHeight = 0;
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}

	}

	private void jump() {
		if (!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}

		/*
		 * if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){ position.y -= 0.16f; }
		 */
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

}
