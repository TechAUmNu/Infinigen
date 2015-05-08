package newGui;

import newModels.RawModel;
import newRendering.Loader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

public class GuiElement {
	
	private RawModel box;
	private int texture;
	public GuiElement(Vector2f topLeft, Vector2f bottomRight, String texture, Loader loader) {
		super();
		generateModel(topLeft, bottomRight, loader);		
		loadTexture(texture, loader);
	}
	private void loadTexture(String textureName, Loader loader) {
		texture = loader.loadTexture(textureName);		
	}
	private void generateModel(Vector2f position, Vector2f size, Loader loader) {
		 float[] positions = {
				 convertCoord(position.x, Display.getWidth()) , convertCoord(position.y+size.y, Display.getHeight()),
				 convertCoord(position.x+size.x, Display.getWidth()), convertCoord(position.y+size.y, Display.getHeight()),
				 convertCoord(position.x, Display.getWidth()), convertCoord(position.y, Display.getHeight()),
				
				 convertCoord(position.x+size.x, Display.getWidth()), convertCoord(position.y, Display.getHeight()),
				 
				 
				 
				
				 
				 
				 
				
				
				
				
				
				
				
				
					
		};
		
		 

		
		float[] textureCoords = {
			0,0,
			
			1,0,
			0,1,
			1,1	
		};		
				
		box = loader.loadToVAO(positions, textureCoords, 2);
		
	}
	
	public int getTexture() {
		return texture;
	}
	
	public RawModel getBox() {
		return box;
	}
	
	
	private float convertCoord(float coord, float maxSize){
		return (((coord * 2) / maxSize) - 1);
	}
	
}
