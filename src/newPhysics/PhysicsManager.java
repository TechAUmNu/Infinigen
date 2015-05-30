package newPhysics;

public class PhysicsManager {

	private PhysicsProcessor processor;
	
	
	public PhysicsManager(){
		processor = new PhysicsProcessor();
	}
	
	
	public PhysicsProcessor getProcessor() {
		return processor;
	}


	public void simulate() {
		processor.simulate();
	}

}
