package newUnitBuilder;

import java.util.ArrayList;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.input.Mouse;

import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import newEntities.PhysicsEntity;
import newMain.Globals;
import newMain.IModule;
import newModels.PhysicsModel;
import newModels.TexturedPhysicsModel;
import newPhysics.PhysicsProcessor;
import newTextures.ModelTexture;
import newUtility.Maths;
import newobjConverter.OBJFileLoader;

public class UnitBuilderManager implements IModule {

	ConstructionArea area;
	PhysicsProcessor processor;
	
	TexturedPhysicsModel boxModel;
	ArrayList<PhysicsEntity> temp;
	
	
	int timeLeft = 0;
	
	
	@Override
	public void setUp() {
		
		temp = new ArrayList<PhysicsEntity>();
		processor = new PhysicsProcessor();
		processor.setUpPhysics(true, true);
		
		
		Unit unit = new Unit();
		unit.setup(processor);
		area = new ConstructionArea(unit);
		
		
		PhysicsModel pmodel = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics("box", Globals.getLoader());
		boxModel = new TexturedPhysicsModel(pmodel, new ModelTexture(Globals.getLoader().loadTexture("box")));
		
	}

	@Override
	public void update() {
		processor.simulate();

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
		
		PhysicsEntity model = new PhysicsEntity(boxModel, Maths.convertVectorBtoL(camPos), 0, 0, 0, 1, 10, processor);
		temp.add(model);
		

		RigidBody body = model.getBody();

		Vector3f linVel = shootRay;	
		linVel.normalize();
		linVel.scale(10);

		
		body.setLinearVelocity(linVel);
		body.setAngularVelocity(new Vector3f(0f, 0f, 0f));

		body.setCcdMotionThreshold(1f);
		body.setCcdSweptSphereRadius(0.2f);
		
		timeLeft = 100;
		
		}
		timeLeft--;
		CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(rayFrom, rayTo);
		processor.getDynamicsWorld().rayTest(rayFrom, rayTo, rayCallback);
		
		
		
		//System.out.println("RayFrom : " + rayFrom);
		//System.out.println("RayTo : " + rayTo);
		if (rayCallback.hasHit()) {
			RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
			
			area.getUnit().highlight(body.hashCode());
			
			
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
		
		ArrayList<PhysicsEntity> todraw = temp;
		 todraw.addAll(unit.getEntities());
		
		
		return (ArrayList<PhysicsEntity>) todraw;
	}

}
