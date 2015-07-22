package main.java.com.ionsystems.infinigen.water;

import main.java.com.ionsystems.infinigen.entities.ICamera;
import main.java.com.ionsystems.infinigen.shaders.ShaderProgram;
import main.java.com.ionsystems.infinigen.utility.Maths;

import org.lwjgl.util.vector.Matrix4f;


public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/main/java/com/ionsystems/infinigen/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "src/main/java/com/ionsystems/infinigen/water/waterFragment.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}


	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(ICamera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

	@Override
	protected void getAllUniformLoactions() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		
	}

}
