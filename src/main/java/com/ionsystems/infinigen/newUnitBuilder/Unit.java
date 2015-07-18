package main.java.com.ionsystems.infinigen.newUnitBuilder;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;







import main.java.com.ionsystems.infinigen.newEntities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.newMain.Globals;
import main.java.com.ionsystems.infinigen.newModels.PhysicsModel;
import main.java.com.ionsystems.infinigen.newModels.TexturedPhysicsModel;
import main.java.com.ionsystems.infinigen.newPhysics.PhysicsProcessor;
import main.java.com.ionsystems.infinigen.newTextures.ModelTexture;
import main.java.com.ionsystems.infinigen.newobjConverter.OBJFileLoader;

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
		
		PhysicsEntity base = new PhysicsEntity(boxModel, new Vector3f(0, 0, 0), 0, 0, 0, 1000, 10, processor);
		entities.add(base);
		System.out.println("Body Hash: "+  base.getBody().hashCode());
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
