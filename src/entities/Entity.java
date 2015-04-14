package entities;

import org.magicwerk.brownies.collections.GapList;

import physics.PhysicsObject;
import world.Block;
import static org.lwjgl.opengl.GL11.*;

public class Entity {
	GapList<Entity> entites;
	GapList<Constraint> constraints;
	GapList<PhysicsObject> bodies;

	
	public Entity(){
		entites = new GapList<Entity>();
		bodies = new GapList<PhysicsObject>();
		constraints = new GapList<Constraint>();
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
			
			glPopMatrix();

		}

	}
}
