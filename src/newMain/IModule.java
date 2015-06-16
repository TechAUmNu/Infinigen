package newMain;

public interface IModule {
	/**
	 * A module is a container for anything that needs to run. The modules are
	 * run in the main game loop so there is a common interface to allow
	 * modding.
	 * 
	 * A module must implement these methods and can add others for additional
	 * functionality.
	 */

	public void process();

	public void setUp();

	public void cleanUp();

	public void render();

	public void update();
}
