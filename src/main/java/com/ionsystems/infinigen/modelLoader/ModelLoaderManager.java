package main.java.com.ionsystems.infinigen.modelLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import main.java.com.ionsystems.infinigen.entities.PhysicsEntity;
import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.global.IModule;
import main.java.com.ionsystems.infinigen.models.PhysicsModel;
import main.java.com.ionsystems.infinigen.objConverter.OBJFileLoader;
import main.java.com.ionsystems.infinigen.utility.FileSearch;

public class ModelLoaderManager implements IModule{

	
	private static final String MODELS_LOCATION = "Models";
	private static final String MODEL_FILE_LIST_NAME = "modellist.txt";
	private ArrayList<ModelFile> modelFiles = new ArrayList<ModelFile>();
	
	
	public ModelLoaderManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void setUp() {
		loadModels();		
	}

	private void loadModels() {
		//Find all model files under the models folder
		//So we need to search recursively under that folder and store all the models found.
		//We can use the modelList.txt file in each folder to know what models there are.
		
		
		List<File> modelLists = FileSearch.searchForFile(MODELS_LOCATION, MODEL_FILE_LIST_NAME);
		
		Path pathBase = new File("../").getAbsoluteFile().toPath();
		//System.out.println(pathBase);
		for(File modelList : modelLists){
			//System.out.println(modelList.toPath());
			Path pathRelative = pathBase.relativize(modelList.toPath());
			String folderName = pathRelative.getName(2).toString();
			String fileName = pathRelative.getFileName().toString();
			//System.out.println(folderName);
			//System.out.println(fileName);
			//System.out.println(pathRelative);
			
			//We need to remove the file name from the relative path
			String pathName = modelList.toString().replace(fileName, "");
			//System.out.println(pathName);
			
			//Now we get the names of the objects in this folder from the file. 
		
			try (Stream<String> lines = Files.lines (modelList.toPath(), StandardCharsets.UTF_8))
			{
			    for (String line : (Iterable<String>) lines::iterator)
			    {
			    	//Now we make a ModelFile object for each one.
			    	modelFiles.add(new ModelFile(pathName + line, folderName, line));			    	
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
		
		//Load them	and the associated textures.
		
		for(ModelFile m : modelFiles){
			//System.out.println(m.getFilePath());
			//System.out.println(m.getFolder() + "/" + m.getSpawnName());	
			PhysicsModel model = OBJFileLoader.loadOBJtoVAOWithGeneratedPhysics(m.getFilePath());
			
			
			String texture = model.getTexture();
			Globals.getLoader().loadTexture(texture); //This just loads the texture now so we get the error on startup
													  //saying the texture is missing rather than halfway through a game
			Globals.addLoadedPhysicsModel(m.getFolder() +"/"+ m.getSpawnName(), model);			
		}
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<PhysicsEntity> prepare() {
		// TODO Auto-generated method stub
		return null;
	}

}
