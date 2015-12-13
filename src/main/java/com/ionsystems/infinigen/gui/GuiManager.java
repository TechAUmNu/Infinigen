package main.java.com.ionsystems.infinigen.gui;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.util.vector.Vector2f;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.rendering.Loader;
import main.java.com.ionsystems.infinigen.utility.Maths;

public class GuiManager implements IModule {

	private GuiRenderer renderer;
	private Loader loader;

	public void generateElement(float x, float y, String texture) {
		renderer.addElement(new GuiElement(Maths.convertCoordinate(new Vector2f(x, y)), texture, loader));
	}

	public void addElement(float x, float y, Vector3f texture) {
		renderer.addElement(new GuiElement(Maths.convertCoordinate(new Vector2f(x, y)), new Vector2f(1024, 1024), new Vector2f(0, 0), texture, loader));

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
