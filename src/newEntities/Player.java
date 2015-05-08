package newEntities;

import javax.vecmath.Quat4f;

import newModels.TexturedModel;
import newModels.TexturedPhysicsModel;
import newPhysics.PhysicsProcessor;
import newRendering.DisplayManager;
import newTerrains.Terrain;
import newUtility.Maths;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class Player extends PhysicsEntity{

	private static final float RUN_SPEED = 1000;
	private static final float TURN_SPEED = 1000;
	private static final float GRAVITY = -10000;
	private static final float JUMP_POWER = 10000;
	private static final float TERRAIN_HEIGHT = 0;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir = false;
	
	public Player(TexturedPhysicsModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale, PhysicsProcessor processor) {
		super(model, position, rotX, rotY, rotZ, scale, scale, processor);		
		super.body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
	}

	public void move(Terrain terrain){
		super.body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
		checkInputs();
		//super.body.setFriction(5);
		super.body.applyTorque(new javax.vecmath.Vector3f(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0));
		
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		Quat4f rotation = super.body.getWorldTransform(new Transform()).getRotation(new Quat4f());
		
		float yRotation = Maths.convertToHeading(rotation);
		float dx = (float) (distance * Math.sin(Math.toRadians(yRotation)));
		float dz = (float) (distance * Math.cos(Math.toRadians(yRotation)));
		
		super.body.applyForce(new javax.vecmath.Vector3f(dx, 0, dz), new javax.vecmath.Vector3f(dx, 0, dz));
		
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		//super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		//super.body.applyForce(new javax.vecmath.Vector3f(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0), new javax.vecmath.Vector3f(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0));
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y < terrainHeight){
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	
	}
	
	private void jump(){
		if(!isInAir){
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}else{
			this.currentSpeed = 0;
		}
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}else{
			this.currentTurnSpeed = 0;
		}
		
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
		
		
		/*
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
			position.y -= 0.16f;
		}
		*/
	}
}
