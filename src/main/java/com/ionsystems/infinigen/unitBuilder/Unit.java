package main.java.com.ionsystems.infinigen.unitBuilder;

import java.util.ArrayList;
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

	List<PhysicsEntity> entities;
	List<TypedConstraint> joints;
	private PhysicsProcessor processor;
	
	public void setup(PhysicsProcessor processor) {
		entities = new ArrayList<PhysicsEntity>();
		joints = new ArrayList<TypedConstraint>();
		this.processor = processor;
		createBaseCube(processor);
	}

	private void createBaseCube(PhysicsProcessor processor) {
		PhysicsEntity base = makeBox(10,10,10,0);
		PhysicsEntity jointTest = makeBox(10,10,20,1);
		Generic6DofConstraint binding = BindEntities(base,jointTest,0.0f,2.3f,0.0f);
		entities.add(base);
		entities.add(jointTest);
		joints.add(binding);
		System.out.println("Body Hash: "+  base.getBody().hashCode());
		
		//Now we can set the camera to look at it.
		Globals.setCameraEntity(base);
	}

	public List<PhysicsEntity> getEntities() {
		return entities;
	}

	public List<TypedConstraint> getJoints() {
		return joints;
	}
	
	public PhysicsEntity makeBox(int x, int y , int z, int mass){
		PhysicsModel pmodel = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics("box", Globals.getLoader());
		TexturedPhysicsModel boxModel = new TexturedPhysicsModel(pmodel, new ModelTexture(Globals.getLoader().loadTexture("box")));
		return new PhysicsEntity(boxModel, new Vector3f(x, y, z), 0, 0, 0, 1, mass, processor);
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

	public void highlight(int hashCode) {
		for(PhysicsEntity entity : entities){
			if(entity.getBody().hashCode() == hashCode){
				entity.highlight(true);				
			}
			else{
				entity.highlight(false);
			}
		}
	}
	
	public boolean IsBodyInUnit(RigidBody searchFor){
		
		for(PhysicsEntity i: entities){
			if(i.getBody().equals(searchFor))return true;
		}
		return false;
	}
	
	
}
