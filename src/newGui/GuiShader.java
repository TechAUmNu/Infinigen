package newGui;

import newShaders.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;



public class GuiShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "src/newGui/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/newGui/guiFragmentShader.txt";
	
	private int location_transformationMatrix;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLoactions() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		
	}
	
	
	

}
