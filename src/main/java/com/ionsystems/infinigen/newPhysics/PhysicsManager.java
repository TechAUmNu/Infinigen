package main.java.com.ionsystems.infinigen.newPhysics;

import java.util.ArrayList;

import main.java.com.ionsystems.infinigen.newEntities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.newMain.Globals;
import main.java.com.ionsystems.infinigen.newMain.IModule;

public class PhysicsManager implements IModule {

	private PhysicsProcessor processor;

	public PhysicsProcessor getProcessor() {
		return processor;
	}

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
		processor.setUpPhysics(true,true);

	}

	@Override
	public void cleanUp() {

		// TODO Auto-generated method stub
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return new ArrayList<PhysicsEntity>();
	}

}
