package main.java.com.ionsystems.infinigen.physics;

import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;

public class PhysicsManager implements IModule {

	private PhysicsProcessor processor;

	public PhysicsProcessor getProcessor() {
		return processor;
	}

	@Override
	public void update() {
		processor.simulate();
		Globals.setBodies(processor.getBodies());
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUp() {
		processor = new PhysicsProcessor();
		processor.setUpPhysics(false, true);

	}

	@Override
	public void cleanUp() {

		// TODO Auto-generated method stub
	}

	@Override
	public void render() {
		if (Globals.debugRendering()) {
			System.out.println("Rendering Debug World");
			processor.getDynamicsWorld().debugDrawWorld();
		}
	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return new ArrayList<PhysicsEntity>();
	}

}
