package main.java.com.ionsystems.infinigen.unitBuilder;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;
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
	int cameraSwitchTimer = 0;
	Unit unit;
	int xOffset;
	int yOffset;
	int zOffset;

	//bind boxes to surrounding boxes
	//make a method that makes a copy of boxes
	//make a method that works out the size of the unit
	
	@Override
	public void setUp() {

		unit = new Unit();
		unit.setup(Globals.getPhysics().getProcessor());
		area = new ConstructionArea(unit);

		PhysicsModel pmodel = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics("box", Globals.getLoader());
		boxModel = new TexturedPhysicsModel(pmodel, new ModelTexture(Globals.getLoader().loadTexture("box")));
		Globals.setPlacementOffset((float) 2.5);
	}

	@Override
	public void update() {
		// processor.simulate();

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

		// SHOOTHING CODE /////////////////
		// Vector3f camPos = new
		// Vector3f(Maths.convertVector(Globals.getCameraPosition()));
		//
		// PhysicsEntity model = new PhysicsEntity(boxModel,
		// Maths.convertVectorBtoL(camPos), 0, 0, 0, 1, 1,
		// Globals.getPhysics().getProcessor());
		// Globals.addEntity(model, false);
		//
		//
		// RigidBody body = model.getBody();
		//
		// Vector3f linVel = shootRay;
		// linVel.normalize();
		// linVel.scale(10);
		//
		//
		// body.setLinearVelocity(linVel);
		// body.setAngularVelocity(new Vector3f(0f, 0f, 0f));
		//
		// body.setCcdMotionThreshold(1f);
		// body.setCcdSweptSphereRadius(0.2f);

		CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(rayFrom, rayTo);
		Globals.getPhysics().getProcessor().getDynamicsWorld().rayTest(rayFrom, rayTo, rayCallback);

		// System.out.println("RayFrom : " + rayFrom);
		// System.out.println("RayTo : " + rayTo);
		RigidBody body;
		if (rayCallback.hasHit()) {
			body = RigidBody.upcast(rayCallback.collisionObject);
			area.getUnit().highlight(body);

			if (Mouse.isButtonDown(0) && timeLeft < 0) {
				

				if (unit.IsBodyInUnit(body) != null) {
					timeLeft = 10;
					javax.vecmath.Vector3f positionBody = new javax.vecmath.Vector3f(0f, 0f, 0f);
					body.getCenterOfMassPosition(positionBody);
					javax.vecmath.Vector3f positionCursor = rayCallback.hitPointWorld;
					//System.out.println(body);
					//System.out.println(positionBody);
					//System.out.println(positionCursor);
					float xDif = positionCursor.x - positionBody.x;
					float yDif = positionCursor.y - positionBody.y;
					float zDif = positionCursor.z - positionBody.z;

					float xDifP = Math.abs(xDif);
					float yDifP = Math.abs(yDif);
					float zDifP = Math.abs(zDif);

					xOffset = 0;
					yOffset = 0;
					zOffset = 0;

					if (xDifP > yDifP && xDifP > zDifP) {
						if (xDif > 0)
							xOffset = 1;
						else
							xOffset = -1;
					}

					if (yDifP > xDifP && yDifP > zDifP) {
						if (yDif > 0)
							yOffset = 1;
						else
							yOffset = -1;
					}

					if (zDifP > yDifP && zDifP > xDifP) {
						if (zDif > 0)
							zOffset = 1;
						else
							zOffset = -1;
					}
					javax.vecmath.Vector3f offsets = new javax.vecmath.Vector3f(xOffset, yOffset, zOffset);
					//System.out.println(offsets);
					float placementOffset = Globals.getPlacementOffset();
					float newPositionx =  (positionBody.x + xOffset * placementOffset);
					float newPositiony =  (positionBody.y + yOffset * placementOffset);
					float newPositionz =  (positionBody.z + zOffset * placementOffset);
					
					//System.out.println("make at" + newPositionx + " " + newPositiony + " " + newPositionz);
					
					if(unit.spaceOcupied(newPositionx, newPositiony, newPositionz)) System.out.println("space ocupied");
					else{
						PhysicsEntity newBox = unit.makeBox( newPositionx, newPositiony, newPositionz, 0);
						unit.entities.add(newBox);
						
						if(unit.spaceOcupied(newPositionx, newPositiony, newPositionz + placementOffset)){
							PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony, newPositionz + placementOffset);
							unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, 0, placementOffset));
						}
						if(unit.spaceOcupied(newPositionx, newPositiony, newPositionz - placementOffset)){
							PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony, newPositionz - placementOffset);
							unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, 0, -placementOffset));
						}
						if(unit.spaceOcupied(newPositionx, newPositiony + placementOffset, newPositionz)){
							PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony + placementOffset, newPositionz);
							unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, placementOffset, 0));
						}
						if(unit.spaceOcupied(newPositionx, newPositiony - placementOffset, newPositionz)){
							PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony - placementOffset, newPositionz);
							unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, -placementOffset, 0));
						}
						if(unit.spaceOcupied(newPositionx + placementOffset, newPositiony, newPositionz)){
							PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx + placementOffset, newPositiony, newPositionz);
							unit.joints.add(unit.BindEntities(newBox, entityNextoNew, placementOffset, 0, 0));
						}
						if(unit.spaceOcupied(newPositionx - placementOffset, newPositiony, newPositionz)){
							PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx - placementOffset, newPositiony, newPositionz);
							unit.joints.add(unit.BindEntities(newBox, entityNextoNew, -placementOffset, 0, 0));
						}
						unit.printJoints();
					}
					

				}
			}

			// Switch Camera
			if (Mouse.isButtonDown(2) && cameraSwitchTimer < 0) {

				if (unit.IsBodyInUnit(body) != null) {
					cameraSwitchTimer = 100;

					Globals.setCameraEntity(unit.IsBodyInUnit(body));

				}

			}
			

		}

		timeLeft--;
		cameraSwitchTimer--;
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			System.out.print("woops");
			Unit cloneTest = unit.Clone(0, 10, 0);
			//cloneTest.makeMass();
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
