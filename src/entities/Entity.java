package entities;

import org.magicwerk.brownies.collections.GapList;

import physics.PhysicsObject;
import world.Block;
import static org.lwjgl.opengl.GL11.*;

public class Entity {
	GapList<Entity> entites;
	GapList<Constraint> constraints;
	GapList<PhysicsObject> bodies;
	int displayList;
	
	public Entity(int displayList){
		entites = new GapList<Entity>();
		bodies = new GapList<PhysicsObject>();
		constraints = new GapList<Constraint>();
		this.displayList = displayList;
	}
	
	public void addConstraint(Constraint c) {

	}

	public void addEntity(Entity e) {

	}

	public void removeEntity(Entity e) {

	}

	public void removeContraint(Constraint c) {

	}

	public void generatePhysicsObject() {
		bodies.add(new PhysicsObject());
	}

	public void draw() {
		for (PhysicsObject p : bodies) {
			glPushMatrix();
			p.translateToPosition();
			
			glCallList(displayList);
			
			glPopMatrix();

		}

	}
}
