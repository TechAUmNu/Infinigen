package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import objConverter.OBJFileLoader;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import newEntities.Camera;
import newEntities.Entity;
import newEntities.Light;
import newEntities.Player;
import newGui.GuiRenderer;
import newGui.GuiTexture;
import newModels.RawModel;
import newModels.TexturedModel;
import newRendering.DisplayManager;
import newRendering.Loader;
import newRendering.MasterRenderer;
import newRendering.EntityRenderer;
import newShaders.StaticShader;
import newTerrains.Terrain;
import newTextures.ModelTexture;
import newTextures.TerrainTexture;
import newTextures.TerrainTexturePack;
import newUtility.MousePicker;

public class MainGameLoop {
	
	
	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", new File("natives/windows").getAbsolutePath());
		DisplayManager.createDisplay();
		Loader loader = new Loader();		
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		RawModel model = OBJFileLoader.loadOBJtoVAO("lamp", loader);
		
		TexturedModel lamp = new TexturedModel(model, new ModelTexture(loader.loadTexture("lamp")));
		TexturedModel grass = new TexturedModel(OBJFileLoader.loadOBJtoVAO("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJFileLoader.loadOBJtoVAO("grassModel", loader), new ModelTexture(loader.loadTexture("flower")));
		
		
		
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		
		TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJtoVAO("fern", loader), fernTextureAtlas);
		
		
		
		
		
		TexturedModel bobble = new TexturedModel(OBJFileLoader.loadOBJtoVAO("lowPolyTree", loader), new ModelTexture(loader.loadTexture("lowPolyTree")));
		
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);
		
		Terrain terrain = new Terrain(0,-1, loader, texturePack,blendMap, "heightMap");
		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random(676452);
		for(int i = 0; i < 400; i++){
			if(i % 2 == 0){
				float x = random.nextFloat() * 800 - 400;
				float z = random.nextFloat() * -600;
				float y = terrain.getHeightOfTerrain(x, z);
				
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0, random.nextFloat() * 360,0,0.9f));
				
			}
			
			
			if ( i % 7 == 0){
				entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),0,0,0,1.8f));
				entities.add(new Entity(flower, new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400),0,0,0,2.3f));
			}
			if(i % 3 == 0){
				
				entities.add(new Entity(bobble, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600),0, random.nextFloat() * 360,0,random.nextFloat() * 0.1f + 0.6f));
				
			}
		}
		
		
		
	
		List<Light> lights = new ArrayList<Light>();
		
		lights.add(new Light(new Vector3f(0,10000,-7000), new Vector3f(1,1,1))); //Sun
		
		lights.add(new Light(new Vector3f(-185,10,-293), new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(370,17,-300), new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));

		entities.add(new Entity(lamp,new Vector3f(-185,-4.7f,-293),0,0,0,1));
		entities.add(new Entity(lamp,new Vector3f(370,4.2f,-300),0,0,0,1));
		entities.add(new Entity(lamp,new Vector3f(293,-6.8f,-305),0,0,0,1));
		
		//Terrain terrain2 = new Terrain(-1,-1, loader, texturePack,blendMap, "heightMap");
		
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		RawModel bunnyModel = OBJFileLoader.loadOBJtoVAO("bunny", loader);
		TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));
		
		Player player = new Player(stanfordBunny, new Vector3f(100, 0 ,-50), 0,0,0,1);
		Camera camera = new Camera(player);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		boolean mouse0, mouse1 = false;
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
		
		while(!Display.isCloseRequested()){			
			camera.move(terrain);
			player.move(terrain);
			picker.update();
			//System.out.println(picker.getCurrentRay());
			renderer.processEntity(player);
			
			
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
				//camera.processMouse(1, 80, -80);				
			}
			
			
			renderer.processTerrain(terrain);
			//renderer.processTerrain(terrain2);
			for(Entity entity : entities){
				renderer.processEntity(entity);
			}
			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
		
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
		
	}
}
