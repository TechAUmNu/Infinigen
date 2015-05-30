package newGui;

import newRendering.Loader;
import newUtility.Maths;

import org.lwjgl.util.vector.Vector2f;

public class GuiManager {
	
	private GuiRenderer renderer;
	private Loader loader;

	
	public void generateElement(float x, float y, String texture){
		renderer.addElement(new GuiElement(Maths.convertCoordinate(new Vector2f(x,y)), texture, loader));
	}
	
	
	public GuiManager(Loader loader){
		renderer = new GuiRenderer();
		this.loader = loader;
	}


	public void cleanUp() {
		renderer.cleanUp();		
	}


	public void render() {
		renderer.render();
		
	}
	
}
