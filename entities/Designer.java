package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import utility.EulerCamera;
import world.ChunkManager;

/**
 * Main designer class which handles creation of entities
 * @author Euan
 *
 */
public class Designer {
	public void initDesigner(ChunkManager cm, EulerCamera camera){
		//Generate a few chunks for a floor
		cm.GenerateChunk(10000, 10000, 10000);
		cm.GenerateChunk(10001, 10000, 10000);
		cm.GenerateChunk(10000, 10001, 10000);
		cm.GenerateChunk(10001, 10001, 10000);
	}
	
	
	public void processKeyboard(){
		while (Keyboard.next()) {
	        if (Keyboard.getEventKeyState()) {
	            if (Keyboard.getEventKey() == Keyboard.KEY_A) {
	            System.out.println("A Key Pressed");
	        }
	        if (Keyboard.getEventKey() == Keyboard.KEY_S) {
	            System.out.println("S Key Pressed");
	        }
	        if (Keyboard.getEventKey() == Keyboard.KEY_D) {
	            System.out.println("D Key Pressed");
	        }
	        } else {
	            if (Keyboard.getEventKey() == Keyboard.KEY_A) {
	            System.out.println("A Key Released");
	            }
	            if (Keyboard.getEventKey() == Keyboard.KEY_S) {
	            System.out.println("S Key Released");
	        }
	        if (Keyboard.getEventKey() == Keyboard.KEY_D) {
	            System.out.println("D Key Released");
	        }
	        }
	    }
		
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
