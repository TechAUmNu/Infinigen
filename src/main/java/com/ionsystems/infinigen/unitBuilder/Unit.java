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

import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.Transform;

public class Unit {

	List<PhysicsEntity> entities;
	List<TypedConstraint> joints;
	
	public void setup(PhysicsProcessor processor) {
		entities = new ArrayList<PhysicsEntity>();
		joints = new ArrayList<TypedConstraint>();
		
		createBaseCube(processor);
	}

	private void createBaseCube(PhysicsProcessor processor) {
			
		PhysicsModel pmodel = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics("box", Globals.getLoader());
		TexturedPhysicsModel boxModel = new TexturedPhysicsModel(pmodel, new ModelTexture(Globals.getLoader().loadTexture("box")));
		
		PhysicsEntity base = new PhysicsEntity(boxModel, new Vector3f(10, 5, 10), 0, 0, 0, 1000, 1, processor);
		PhysicsEntity jointTest = new PhysicsEntity(boxModel, new Vector3f(20, 5, 10), 0, 0, 0, 1000, 1, processor);
		Transform frameInA, frameInB;
		frameInA = new Transform();
		frameInB = new Transform();
		frameInA.setIdentity();
		frameInB.setIdentity();
		frameInA.origin.set(0f,10f,10f);
		frameInB.origin.set(0f,10f,10f);
		Generic6DofConstraint x = new Generic6DofConstraint(base.getBody(),jointTest.getBody(),frameInA,frameInB,true);
		javax.vecmath.Vector3f z = new javax.vecmath.Vector3f(0f,0f,0f);
		x.setAngularLowerLimit(z);
		x.setAngularUpperLimit(z);
		x.setLinearLowerLimit(z);
		x.setLinearUpperLimit(z);
		processor.addConstraint((TypedConstraint)x);
		entities.add(base);
		entities.add(jointTest);
		joints.add(x);
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
	
	
	
	
}
