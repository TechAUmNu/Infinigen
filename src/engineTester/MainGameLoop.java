package engineTester;

import java.io.File;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import newEntities.Camera;
import newEntities.Entity;
import newModels.RawModel;
import newModels.TexturedModel;
import newRendering.DisplayManager;
import newRendering.Loader;
import newRendering.OBJLoader;
import newRendering.Renderer;
import newShaders.StaticShader;
import newTextures.ModelTexture;

public class MainGameLoop {
	
	
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", new File("natives/windows").getAbsolutePath());
		DisplayManager.createDisplay();
		Loader loader = new Loader();		
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
		
		
		RawModel model = OBJLoader.loadObjModel("stall", loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		
		Entity entity = new Entity(texturedModel, new Vector3f(0,0,-50),0,0,0,1);
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()){	
			entity.increaseRotation(0f, 0.5f, 0f);
			camera.move();
			renderer.prepare();
			shader.start();
			shader.loadViewMatrix(camera);
			renderer.render(entity, shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
		
	}
}
