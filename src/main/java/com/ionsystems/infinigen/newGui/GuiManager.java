package main.java.com.ionsystems.infinigen.newGui;

import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.newEntities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.newMain.IModule;
import main.java.com.ionsystems.infinigen.newRendering.Loader;
import main.java.com.ionsystems.infinigen.newUtility.Maths;

import org.lwjgl.util.vector.Vector2f;

public class GuiManager implements IModule {

	private GuiRenderer renderer;
	private Loader loader;

	public void generateElement(float x, float y, String texture) {
		renderer.addElement(new GuiElement(Maths.convertCoordinate(new Vector2f(x, y)), texture, loader));
	}

	public GuiManager(Loader loader) {
		renderer = new GuiRenderer();
		this.loader = loader;
	}

	@Override
	public void cleanUp() {
		renderer.cleanUp();
	}

	@Override
	public void render() {
		renderer.render();

	}

	@Override
	public void update() {
		// TODO I guess we could put stuff for button animations here?
	}

	@Override
	public void process() {
		// TODO Check if each button has been clicked

	}

	@Override
	public void setUp() {
		// TODO Gui Setup code here

	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}

}