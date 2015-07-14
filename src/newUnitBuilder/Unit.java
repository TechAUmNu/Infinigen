package newUnitBuilder;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;







import newEntities.PhysicsEntity;
import newMain.Globals;
import newModels.PhysicsModel;
import newModels.TexturedPhysicsModel;
import newPhysics.PhysicsProcessor;
import newTextures.ModelTexture;
import newobjConverter.OBJFileLoader;

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
