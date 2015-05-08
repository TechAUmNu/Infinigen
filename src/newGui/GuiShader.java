package newGui;

import newShaders.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;



public class GuiShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "src/newGui/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/newGui/guiFragmentShader.txt";
	
	
	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	


	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	@Override
	protected void getAllUniformLoactions() {
		
		
	}
	
	
	

}
