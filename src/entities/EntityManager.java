package entities;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.magicwerk.brownies.collections.GapList;

import physics.PhysicsManager;
import physics.PhysicsObject;

import com.bulletphysics.dynamics.RigidBody;



public class EntityManager {

private static EntityManager instance;
private int cubeList;

	static {
		setInstance(new EntityManager());
	}	
	
	//Initialise variables.
	private EntityManager() {
		genDisplayList();
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
		
		
		
		
		Entity e = new Entity(cubeList);

		PhysicsObject p = new PhysicsObject();
		p.body = rigidBody;
		e.bodies.add(p);
		entities.add(e);
		PhysicsManager.getInstance().addPhysicsObject(rigidBody, id);		
	}
	public int numEntities(){
		return entities.size();
	}

	private void genDisplayList() {
		cubeList = glGenLists(1);
		glNewList(cubeList, GL_COMPILE);
			glBegin(GL_QUADS);
			
			glVertex3f(1.0f, 1.0f, -1.0f);
			glVertex3f(-1.0f, 1.0f, -1.0f);
			glVertex3f(-1.0f, 1.0f, 1.0f);
			glVertex3f(1.0f, 1.0f, 1.0f);
			
			glVertex3f(1.0f, -1.0f, 1.0f);
			glVertex3f(-1.0f, -1.0f, 1.0f);
			glVertex3f(-1.0f, -1.0f, -1.0f);
			glVertex3f(1.0f, -1.0f, -1.0f);
			
			glVertex3f(1.0f, 1.0f, 1.0f);
			glVertex3f(-1.0f, 1.0f, 1.0f);
			glVertex3f(-1.0f, -1.0f, 1.0f);
			glVertex3f(1.0f, -1.0f, 1.0f);
			
			glVertex3f(1.0f, -1.0f, -1.0f);
			glVertex3f(-1.0f, -1.0f, -1.0f);
			glVertex3f(-1.0f, 1.0f, -1.0f);
			glVertex3f(1.0f, 1.0f, -1.0f);
			
			glVertex3f(-1.0f, 1.0f, 1.0f);
			glVertex3f(-1.0f, 1.0f, -1.0f);
			glVertex3f(-1.0f, -1.0f, -1.0f);
			glVertex3f(-1.0f, -1.0f, 1.0f);
			
			glVertex3f(1.0f, 1.0f, -1.0f);
			glVertex3f(1.0f, 1.0f, 1.0f);
			glVertex3f(1.0f, -1.0f, 1.0f);
			glVertex3f(1.0f, -1.0f, -1.0f);
		glEnd();
	glEndList();
	}
}
