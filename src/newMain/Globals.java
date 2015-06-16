package newMain;

import newRendering.Loader;

public class Globals {

	private static float gravity = 9.81f;
	private static Loader loader;

	// //////////////////////////////////////////////////////////////////////////////////////

	public static float getGravity() {
		return gravity;
	}

	public static void setGravity(float gravity) {
		Globals.gravity = gravity;
	}

	public static Loader getLoader() {
		return loader;
	}

	public static void setLoader(Loader loader) {
		Globals.loader = loader;
	}

}
