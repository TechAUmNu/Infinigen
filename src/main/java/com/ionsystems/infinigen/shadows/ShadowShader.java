package main.java.com.ionsystems.infinigen.shadows;

import main.java.com.ionsystems.infinigen.shaders.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;


public class ShadowShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/shadowVertex.txt";
	private final static String FRAGMENT_FILE = "res/shaders/shadowFragment.txt";

	
	private int location_depthMatrix;
	private int location_depthModelMatrix;

	public ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}
	
	public void loadDepthMatrix(Matrix4f depthMatrix) {
		loadMatrix(location_depthMatrix, depthMatrix);
	}
	
	public void loadDepthModelMatrix(Matrix4f depthModelMatrix){		
		loadMatrix(location_depthModelMatrix, depthModelMatrix);
	}

	public void loadDepthModelMatrix(float[] depthModelMatrix) {
		super.loadMatrix(location_depthModelMatrix, depthModelMatrix);
	}
	

	@Override
	protected void getAllUniformLoactions() {
		location_depthMatrix = getUniformLocation("depthMatrix");
		location_depthModelMatrix = getUniformLocation("depthModelMatrix");
		
	}
	
	

}
