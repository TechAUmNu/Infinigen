package main.java.com.ionsystems.infinigen.unitBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.RigidBody;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.global.Units;
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

	// bind boxes to surrounding boxes
	// make a method that makes a copy of boxes
	// make a method that works out the size of the unit

	@Override
	public void setUp() {

		unit = new Unit();
		unit.setup(Globals.getPhysics().getProcessor());
		area = new ConstructionArea(unit);

		PhysicsModel pmodel = Globals.getModel("base/cube1x1");

		boxModel = new TexturedPhysicsModel(pmodel, new ModelTexture(Globals.getLoader().loadTexture("box")));
		Globals.setPlacementOffset((float) 2.05);
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

				placeBoxWIthMouse(rayCallback, body, 3, 1, 1);
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
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			System.out.print("woops");
			Unit cloneTest = unit.Clone(0, 10, 0);
			Units.addUnit(cloneTest);
			cloneTest.makeMass();
		}
	}

	private void placeBoxWIthMouse(CollisionWorld.ClosestRayResultCallback rayCallback, RigidBody body, int x, int y, int z) {
		float placementOffset = Globals.getPlacementOffset();
		/**
		 * This needs to be done with the LocationID not with center of mass.
		 * 
		 * It should find the unit that is being picked and then work out where
		 * on that unit is being pointed at. Then it needs to know which way to
		 * place the new object (ie some way to rotate it) Once the mouse is
		 * clicked then it should work out what other objects are around the
		 * placed object by searching the unit by distance from the center of
		 * the placed object. (Or there could be a manual mode to select the
		 * objects it gets joined too.)
		 * 
		 * Since we know the size of the object we can simply check the hashmap
		 * for each of the locations that would be occupied by the object to be
		 * placed, and once the object is placed we just fill those locations
		 * with the placed object.
		 */
		if (unit.IsBodyInUnit(body) != null) {
			timeLeft = 0;
			javax.vecmath.Vector3f positionBody = new javax.vecmath.Vector3f(0f, 0f, 0f);
			body.getCenterOfMassPosition(positionBody);
			PhysicsEntity baseEntity = unit.getEntity(body);

			Vector3f positionCursor = rayCallback.hitPointWorld;
			// System.out.println(body);
			// System.out.println(positionBody);
			// System.out.println(positionCursor);

			List<LocationID> gridPoints = baseEntity.gridPoints;

			float minDistance = 1000000;
			LocationID minGridPoint = null;
			for (LocationID location : gridPoints) {
				float xDif = positionCursor.x - location.x;
				float yDif = positionCursor.y - location.y;
				float zDif = positionCursor.z - location.z;
				float total = xDif + yDif + zDif;

				if (total < minDistance) {
					minDistance = total;
					minGridPoint = location;
				}
			}

			float xBaseOffset = (minGridPoint.x - positionBody.x) * placementOffset;
			float yBaseOffset = (minGridPoint.y - positionBody.y) * placementOffset;
			float zBaseOffset = (minGridPoint.z - positionBody.z) * placementOffset;

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

			javax.vecmath.Vector3f offsets = new javax.vecmath.Vector3f(xOffset * placementOffset, yOffset * placementOffset, zOffset * placementOffset);
			System.out.println("Offsets Before Modification: " + offsets);
			offsets.x += xBaseOffset;
			offsets.y += yBaseOffset;
			offsets.z += zBaseOffset;

			if (x > 1) {
				if (xOffset > 0) {
					offsets.x += (((float) x + 1) / 2.0) * placementOffset;
				}
				if (xOffset < 0) {
					offsets.x -= (((float) x + 1) / 2.0) * placementOffset;
				}
			}
			if (y > 1) {
				if (yOffset > 0) {
					offsets.y += (((float) y + 1) / 2.0) * placementOffset;
				}
				if (yOffset < 0) {
					offsets.y -= (((float) y + 1) / 2.0) * placementOffset;
				}
			}
			if (z > 1) {
				if (zOffset > 0) {
					offsets.z += (((float) z + 1) / 2.0) * placementOffset;
				}
				if (zOffset < 0) {
					offsets.z -= (((float) z + 1) / 2.0) * placementOffset;
				}
			}
			System.out.println("Offsets After Modification: " + offsets);

			// System.out.println(offsets);

			float newPositionx = (positionBody.x + offsets.x);
			float newPositiony = (positionBody.y + offsets.y);
			float newPositionz = (positionBody.z + offsets.z);

			System.out.println("New Position: " + newPositionx + ", " + newPositiony + ", " + newPositionz);

			// System.out.println("make at" + newPositionx + " " + newPositiony
			// + " " + newPositionz);

			if (unit.spaceOcupied(newPositionx, newPositiony, newPositionz))
				System.out.println("space ocupied");
			else {
				PhysicsEntity newBox = unit.makeBox(newPositionx, newPositiony, newPositionz, 0, "base/cube1x3");
				unit.entities.add(newBox);

				if (unit.spaceOcupied(newPositionx, newPositiony, newPositionz + placementOffset)) {
					PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony, newPositionz + placementOffset);
					unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, 0, placementOffset));
				}
				if (unit.spaceOcupied(newPositionx, newPositiony, newPositionz - placementOffset)) {
					PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony, newPositionz - placementOffset);
					unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, 0, -placementOffset));
				}
				if (unit.spaceOcupied(newPositionx, newPositiony + placementOffset, newPositionz)) {
					PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony + placementOffset, newPositionz);
					unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, placementOffset, 0));
				}
				if (unit.spaceOcupied(newPositionx, newPositiony - placementOffset, newPositionz)) {
					PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx, newPositiony - placementOffset, newPositionz);
					unit.joints.add(unit.BindEntities(newBox, entityNextoNew, 0, -placementOffset, 0));
				}
				if (unit.spaceOcupied(newPositionx + placementOffset, newPositiony, newPositionz)) {
					PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx + placementOffset, newPositiony, newPositionz);
					unit.joints.add(unit.BindEntities(newBox, entityNextoNew, placementOffset, 0, 0));
				}
				if (unit.spaceOcupied(newPositionx - placementOffset, newPositiony, newPositionz)) {
					PhysicsEntity entityNextoNew = unit.getBoxAt(newPositionx - placementOffset, newPositiony, newPositionz);
					unit.joints.add(unit.BindEntities(newBox, entityNextoNew, -placementOffset, 0, 0));
				}
				unit.printJoints();
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
