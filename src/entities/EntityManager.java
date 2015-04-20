package entities;

import org.magicwerk.brownies.collections.GapList;

import physics.PhysicsManager;
import physics.PhysicsObject;

import com.bulletphysics.dynamics.RigidBody;



public class EntityManager {

private static EntityManager instance;
	
	static {
		setInstance(new EntityManager());
	}	
	
	//Initialise variables.
	private EntityManager() {				
	}	

	public static EntityManager getInstance() {
		return instance;
	}

	public static void setInstance(EntityManager instance) {
		EntityManager.instance = instance;
	}
	
	
	private GapList<Entity> entities = new GapList<Entity>();
	
	
	public void loadEntity(Entity e){
		
	}
	
	public void saveEntity(Entity e){
		
	}
	
	public void deleteEntity(Entity e){
		
	}

	
	public void drawAll() {
		for(Entity e : entities){
			e.draw();
		}
		
	}

	public void process(float delta) {
		// TODO Auto-generated method stub
		
	}

	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	public void addEntity(RigidBody rigidBody, int id) {
		Entity e = new Entity();
		PhysicsObject p = new PhysicsObject();
		p.body = rigidBody;
		e.bodies.add(p);
		entities.add(e);
		PhysicsManager.getInstance().addPhysicsObject(rigidBody, id);		
	}
	public int numEntities(){
		return entities.size();
	}

	
}
