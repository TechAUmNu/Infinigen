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
		processor.setUpPhysics(true);
		
		
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
		
		if(Mouse.isButtonDown(2) && timeLeft < 0){
		
		Vector3f destination = Maths.convertVector(Globals.getMouseRay());
		
		float mass = 1f;
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		Vector3f camPos = new Vector3f(Maths.convertVector(Globals.getCameraPosition()));
		startTransform.origin.set(camPos);

		
			
			
			PhysicsEntity model = new PhysicsEntity(boxModel, Maths.convertVectorBtoL(camPos), 0, 0, 0, 1, 10, processor);
			temp.add(model);
		

		RigidBody body = model.getBody();

		Vector3f linVel = destination;
		System.out.println(linVel);
		linVel.normalize();
		linVel.scale(10);

		Transform worldTrans = body.getWorldTransform(new Transform());
		worldTrans.origin.set(camPos);
		worldTrans.setRotation(new Quat4f(0f, 0f, 0f, 1f));
		body.setWorldTransform(worldTrans);
		
		body.setLinearVelocity(linVel);
		body.setAngularVelocity(new Vector3f(0f, 0f, 0f));

		body.setCcdMotionThreshold(1f);
		body.setCcdSweptSphereRadius(0.2f);
		
		timeLeft = 1;
		
		}
		timeLeft--;
		CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(Maths.convertVector(Globals.getCameraPosition()), Maths.convertVector(Globals.getMouseRay()));
		processor.getDynamicsWorld().rayTest(Maths.convertVector(Globals.getCameraPosition()), Maths.convertVector(Globals.getMousePhysicsRay()), rayCallback);
		

		
		System.out.println(Maths.convertVector(Globals.getCameraPosition()));
		System.out.println(Maths.convertVector(Globals.getMousePhysicsRay()));
		if (rayCallback.hasHit()) {
			RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
			Vector3f com = new Vector3f();
			com = body.getCenterOfMassPosition(com);
			System.out.println(com);
			
			
			System.out.println("WE HIT IT!!");
			Vector3f hitPointWorld = rayCallback.hitPointWorld;
			System.out.println(hitPointWorld);
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
		
		ArrayList<PhysicsEntity> todraw = new ArrayList<PhysicsEntity>();
		 todraw.addAll(unit.getEntities());
		
		
		return (ArrayList<PhysicsEntity>) unit.getEntities();
	}

}
