package entities;

import org.lwjgl.input.Mouse;

/**
 * Main designer class which handles creation of entities
 * @author Euan
 *
 */
public class Designer {
	public void initDesigner(){
		//Generate a few chunks for a floor
	}
	
	
	public void processKeyboard(){
		//process keyboard inputs for changing block etc
	}
	public static void processMouse(){
		//Process mouse for changing block/placing deleting blocks
		while(Mouse.next()) {
		    if (Mouse.getEventButton() > -1) {
		        if (Mouse.getEventButtonState()) {
		            System.out.println("PRESSED MOUSE BUTTON: " + Mouse.getEventButton());
		        }
		        else System.out.println("RELEASED MOUSE BUTTON: " + Mouse.getEventButton());
		    }
		}
	}
	
	
	public void addBlock(Position p){
		//Adds a block to the current entity
	}
	
	public void removeBlock(Position p){
		//Removes a block from the current entity
	}
	
	
	
	public void generateEntity(Entity e){
		
	}
	
	public void setEntity(Entity e){
		
	}
	
	
	
}
