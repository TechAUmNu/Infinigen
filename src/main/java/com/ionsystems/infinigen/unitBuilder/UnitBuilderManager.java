package main.java.com.ionsystems.infinigen.unitBuilder;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.RigidBody;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.objConverter.OBJFileLoader;
import main.java.com.ionsystems.infinigen.textures.ModelTexture;
import main.java.com.ionsystems.infinigen.utility.Maths;

public class UnitBuilderManager implements IModule {

	ConstructionArea area;
	
	
	TexturedPhysicsModel boxModel;
	
	RigidBody bodyStore;
	
	int timeLeft = 0;
	Unit unit;
	int xOffset;
	int yOffset;
	int zOffset;
	
	@Override
	public void setUp() {
		
		
		
		
		unit = new Unit();
		unit.setup(Globals.getPhysics().getProcessor());
		area = new ConstructionArea(unit);
		
		
		PhysicsModel pmodel = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics("box", Globals.getLoader());
		boxModel = new TexturedPhysicsModel(pmodel, new ModelTexture(Globals.getLoader().loadTexture("box")));
		
	}

	@Override
	public void update() {
		//processor.simulate();

	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		mousePointing();
		
		

	}

	private void mousePointing() {
		
		Vector3f rayFrom = Maths.convertVector(Globals.getCameraPosition());
		Vector3f rayTo = Maths.convertVector(Globals.getMouseRay());
		Vector3f shootRay = Maths.convertVector(Globals.getMouseRay());
		
		rayTo.scale(1000);
		rayTo.add(rayFrom);
		
		
		if(Mouse.isButtonDown(2) && timeLeft < 0){
		
		
	
		Vector3f camPos = new Vector3f(Maths.convertVector(Globals.getCameraPosition()));
		
		PhysicsEntity model = new PhysicsEntity(boxModel, Maths.convertVectorBtoL(camPos), 0, 0, 0, 1, 10, Globals.getPhysics().getProcessor());
		Globals.addEntity(model, false);
		

		RigidBody body = model.getBody();

		Vector3f linVel = shootRay;	
		linVel.normalize();
		linVel.scale(100);

		
		body.setLinearVelocity(linVel);
		body.setAngularVelocity(new Vector3f(0f, 0f, 0f));

		body.setCcdMotionThreshold(1f);
		body.setCcdSweptSphereRadius(0.2f);
		
		timeLeft = 10;
		
		}
		timeLeft--;
		CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(rayFrom, rayTo);
		Globals.getPhysics().getProcessor().getDynamicsWorld().rayTest(rayFrom, rayTo, rayCallback);
		
		
		
		//System.out.println("RayFrom : " + rayFrom);
		//System.out.println("RayTo : " + rayTo);
		if (rayCallback.hasHit()) {
			RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
			
			area.getUnit().highlight(body.hashCode());
			if(unit.IsBodyInUnit(body)){
				
				if(!body.equals(bodyStore)){
					javax.vecmath.Vector3f positionBody = new javax.vecmath.Vector3f(0f,0f,0f);
					body.getCenterOfMassPosition(positionBody);
					javax.vecmath.Vector3f positionCursor = rayCallback.hitPointWorld;
					System.out.println(body);
					System.out.println(positionBody);
					System.out.println(positionCursor);
					float xDif = positionCursor.x - positionBody.x;
					float yDif = positionCursor.y - positionBody.y;
					float zDif = positionCursor.z - positionBody.z;
					
					float xDifP = Math.abs(xDif);
					float yDifP = Math.abs(yDif);
					float zDifP = Math.abs(zDif);
					
					xOffset = 0;
					yOffset = 0;
					zOffset = 0;
					
					if(xDifP > yDifP && xDifP > zDifP){
						if(xDif > 0) xOffset = 1;
						else xOffset = -1;
					}
					
					if(yDifP > xDifP && yDifP > zDifP){
						if(yDif > 0) yOffset = 1;
						else yOffset = -1;
					}
					
					if(zDifP > yDifP && zDifP > xDifP){
						if(zDif > 0) zOffset = 1;
						else zOffset = -1;
					}
					javax.vecmath.Vector3f offsets = new javax.vecmath.Vector3f(xOffset,yOffset,zOffset);
					System.out.println(offsets);
					
					PhysicsEntity newBox = unit.makeBox((int)(positionBody.x + xOffset * 10), (int)(positionBody.y + yOffset * 10), (int)(positionBody.z + zOffset * 10), 1);
					unit.entities.add(newBox);
					bodyStore = body;
				}
			}
		}
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	/**
	 * Here we add all the entities to be drawn
	 */
	public ArrayList<PhysicsEntity> prepare() {
		Unit unit = area.getUnit();
		
		
		
		return (ArrayList<PhysicsEntity>) unit.getEntities();
	}

}
