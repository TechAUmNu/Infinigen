package main.java.com.ionsystems.infinigen.physics;

import main.java.com.ionsystems.infinigen.cameras.ICamera;
import main.java.com.ionsystems.infinigen.shaders.ShaderProgram;
import main.java.com.ionsystems.infinigen.utility.Maths;

import org.lwjgl.util.vector.Matrix4f;


public class PhysicsDebugShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/main/java/com/ionsystems/infinigen/physics/PhysicsDebugVertex.txt";
	private final static String FRAGMENT_FILE = "src/main/java/com/ionsystems/infinigen/physics/PhysicsDebugFragment.txt";

	
	private int location_projectionMatrix;
	private int location_viewMatrix;

	public PhysicsDebugShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}
	
	
	

	@Override
	protected void getAllUniformLoactions() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}
