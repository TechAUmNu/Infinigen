package main.java.com.ionsystems.infinigen.unitBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.models.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.objConverter.OBJFileLoader;
import main.java.com.ionsystems.infinigen.physics.PhysicsProcessor;
import main.java.com.ionsystems.infinigen.textures.ModelTexture;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.Transform;

public class Unit {

	private String name;
	List<PhysicsEntity> entities;
	List<TypedConstraint> joints;
	private PhysicsProcessor processor;
	private HashMap<LocationID,PhysicsEntity> points = new HashMap<>();

	
	public void setup(PhysicsProcessor processor) {
		entities = new ArrayList<PhysicsEntity>();
		joints = new ArrayList<TypedConstraint>();
		this.processor = processor;
		createBaseCube(processor);
	}
	
	public void setupClone(PhysicsProcessor processor) {
		entities = new ArrayList<PhysicsEntity>();
		joints = new ArrayList<TypedConstraint>();
		this.processor = processor;
	}
	
	public PhysicsEntity getEntity(RigidBody body){
		for (PhysicsEntity entity : entities){
			if(entity.getBody().equals(body))
				return entity;			
		}
		return null;
	}

	private void createBaseCube(PhysicsProcessor processor) {
		PhysicsEntity base = makeBox(10,100,10,0, "base/cube1x3");
		//PhysicsEntity jointTest = makeBox(10,10,20,1);
		//Generic6DofConstraint binding = BindEntities(base,jointTest,0.0f,2.3f,0.0f);
		entities.add(base);
		//entities.add(jointTest);
		//joints.add(binding);
		//System.out.println("Body Hash: "+  base.getBody().hashCode());
		
		//Now we can set the camera to look at it.
		Globals.setCameraEntity(base);
	}

	public List<PhysicsEntity> getEntities() {
		return entities;
	}

	public List<TypedConstraint> getJoints() {
		return joints;
	}
	
	public PhysicsEntity makeBox(float x, float y , float z, int mass, String unitType){
		
		TexturedPhysicsModel boxModel = Globals.getTexturedModel(unitType);
		PhysicsEntity store = new PhysicsEntity(boxModel, new Vector3f(x, y, z), 0, 0, 0, boxModel.getPhysicsModel().getScale(), mass, processor, 1, 1, 1);

		for(LocationID i : store.gridPoints){
			points.put(i,store);
		}
		return store;
	}
	
	public Generic6DofConstraint BindEntities(PhysicsEntity entity1, PhysicsEntity entity2, float x, float y , float z){
		Transform frameInA, frameInB;
		frameInA = new Transform();
		frameInB = new Transform();
		frameInA.setIdentity();
		frameInB.setIdentity();
		frameInA.origin.set(x,y,z);
		frameInB.origin.set(0f,0f,0f);
		Generic6DofConstraint binding = new Generic6DofConstraint(entity1.getBody(),entity2.getBody(),frameInA,frameInB,true);
		javax.vecmath.Vector3f lock = new javax.vecmath.Vector3f(0f,0f,0f);
		binding.setAngularLowerLimit(lock);
		binding.setAngularUpperLimit(lock);
		binding.setLinearLowerLimit(lock);
		binding.setLinearUpperLimit(lock);
		
		processor.addConstraint((TypedConstraint)binding);
		return binding;
	}

	public void highlight(RigidBody body) {
		for(PhysicsEntity entity : entities){
			if(entity.getBody().equals(body)){
				entity.highlight(true);				
			}
			else{
				entity.highlight(false);
			}
		}
	}
	
	
	public boolean spaceOcupied(float x,float y,float z){ //check if a space is ocupied
		LocationID store = new LocationID(x,y,z);
		return points.containsKey(store);
	}
	
	public PhysicsEntity getBoxAt(float x,float y,float z){
		LocationID store = new LocationID(x,y,z);
		return points.get(store);
	}
	
	public void printJoints(){ 
		//System.out.println("new check");
		for(TypedConstraint i: joints){
			//System.out.println("joint " + i);
		}

	}
	
	public PhysicsEntity IsBodyInUnit(RigidBody searchFor){
		
		for(PhysicsEntity i: entities){
			if(i.getBody().equals(searchFor))return i;
		}
		return null;
	}
	

	public Unit Clone(float x, float y, float z){
		Unit newUnit = new Unit();
		newUnit.setupClone(Globals.getPhysics().getProcessor());
		//System.out.println(entities.size());
		for(PhysicsEntity i: entities){
			PhysicsEntity newBox = makeBox(i.getPosition().x + x, i.getPosition().y + y, i.getPosition().z + z, 0, "base/cube1x3");
			//System.out.println(i.getPosition().x + " " + i.getPosition().y  + " " + i.getPosition().z);
			//System.out.println(newBox.getPosition().x + " " + newBox.getPosition().y  + " " + newBox.getPosition().z);
			newUnit.entities.add(newBox);
			
		}		
		//System.out.println(newUnit.entities.size());
		
		
		//Must make a name for the Units manager so make a temporary one here
		newUnit.setName("ClonedUnit");
		return newUnit;
	}
	
	public void makeMass(){
		
		for(PhysicsEntity i: entities){
			//System.out.println(i.getBody().getInvMass());
			
			processor.removePhysicsEntity(i);
			
			javax.vecmath.Vector3f inertia = new javax.vecmath.Vector3f(0f,0f,0f);;
			i.getBody().getCollisionShape().calculateLocalInertia(i.getModel().getPhysicsModel().getMass(), inertia);
			i.getBody().setMassProps(i.getModel().getPhysicsModel().getMass(), inertia);
			//System.out.println(i.getBody().getInvMass());
			
			processor.addPhysicsEntity(i);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
