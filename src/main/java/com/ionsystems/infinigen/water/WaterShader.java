package main.java.com.ionsystems.infinigen.water;

import main.java.com.ionsystems.infinigen.cameras.ICamera;
import main.java.com.ionsystems.infinigen.shaders.ShaderProgram;
import main.java.com.ionsystems.infinigen.utility.Maths;

import org.lwjgl.util.vector.Matrix4f;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/waterVertex.txt";
	private final static String FRAGMENT_FILE = "res/shaders/waterFragment.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_moveFactor;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	public void loadMoveFactor(float factor) {
		super.loadFloat(location_moveFactor, factor);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadModelMatrix(Matrix4f modelMatrix) {
		loadMatrix(location_modelMatrix, modelMatrix);
	}

	@Override
	protected void getAllUniformLoactions() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
	}

	public void connectTextureUnits() {
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
	}

}
