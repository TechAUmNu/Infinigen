package newPhysics;

import java.util.ArrayList;

import newEntities.PhysicsEntity;
import newMain.IModule;

public class PhysicsManager implements IModule {

	private PhysicsProcessor processor;

	public PhysicsProcessor getProcessor() {
		return processor;
	}

	public void update() {
		processor.simulate();
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
