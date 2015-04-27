package engineTester;

import java.io.File;

import objConverter.OBJFileLoader;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import newEntities.Camera;
import newEntities.Entity;
import newEntities.Light;
import newModels.RawModel;
import newModels.TexturedModel;
import newRendering.DisplayManager;
import newRendering.Loader;
import newRendering.MasterRenderer;
import newRendering.EntityRenderer;
import newShaders.StaticShader;
import newTerrains.Terrain;
import newTextures.ModelTexture;

public class MainGameLoop {
	
	
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", new File("natives/windows").getAbsolutePath());
		DisplayManager.createDisplay();
		Loader loader = new Loader();		
		
		
		
		
		RawModel model = OBJFileLoader.loadOBJtoVAO("stall", loader);
		ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		
		texture.setShineDamper(10);
		texture.setReflectivity(1);
		
		Entity entity = new Entity(texturedModel, new Vector3f(0,0,-25),0,0,0,1);
		Light light = new Light(new Vector3f(3000,2000,2000), new Vector3f(1,1,1));
		
		Terrain terrain = new Terrain(0,-1, loader, new ModelTexture(loader.loadTexture("grassNormal")));
		Terrain terrain2 = new Terrain(1,0, loader, new ModelTexture(loader.loadTexture("grassNormal")));
		
		Camera camera = new Camera();
		
		
		MasterRenderer renderer = new MasterRenderer();
		
		boolean mouse0, mouse1 = false;
		while(!Display.isCloseRequested()){	
			entity.increaseRotation(0f, 0.5f, 0f);
			camera.move();
			
			if (Mouse.isButtonDown(1) && !mouse1) {
				Mouse.setGrabbed(true);
				mouse1 = true;
			}
			

			// Process Mouse events
			while (Mouse.next()) {
				if (Mouse.getEventButton() == 1) {
					if (!Mouse.getEventButtonState()) {
						Mouse.setGrabbed(false);
						mouse1 = false;
					}
				}
			}
			
			if (Mouse.isGrabbed()) {
				camera.processMouse(1, 80, -80);				
			}
			
			
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.render(light, camera);
			renderer.processEntity(entity);
			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
		
	}
}
