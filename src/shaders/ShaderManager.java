package shaders;

import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;

import utility.ShaderProgram;


public class ShaderManager {

private static ShaderManager instance;
	
	static {
		setInstance(new ShaderManager());
	}	
	
	//Initialise variables.
	private ShaderManager() {	
		loadShaders();
	}	

	public static ShaderManager getInstance() {
		return instance;
	}

	public static void setInstance(ShaderManager instance) {
		ShaderManager.instance = instance;
	}
	
	
	
	
	private String CHUNK_VERTEX_SHADER = "shaders/landscape.vs";
	private String CHUNK_FRAGMENT_SHADER = "shaders/landscape.fs";
			
	
	private HashMap<Shader,ShaderProgram> shaders;
	
	public void loadShaders(){
		//Initialize the Map
		shaders = new HashMap<Shader,ShaderProgram>();
		//Load the Chunk Shader
		
		
		shaders.put(Shader.Chunk, new ShaderProgram());
		shaders.get(Shader.Chunk).attachVertexShader(CHUNK_VERTEX_SHADER);
		shaders.get(Shader.Chunk).attachFragmentShader(CHUNK_FRAGMENT_SHADER);
		shaders.get(Shader.Chunk).link();
	

		
		
	}

	public ShaderProgram getShaderProgram(Shader shader) {
		return shaders.get(shader);
	}
}
