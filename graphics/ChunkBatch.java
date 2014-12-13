package graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.magicwerk.brownies.collections.GapList;

import utility.ShaderProgram;


// A batch is a group of objects that need to be rendered. 
// The job of the batch is to allow all the triangles to be drawn using as few draw calls as possible. 
// Therefore reducing the load on the cpu.

// A batch is specific to a shader.
public class ChunkBatch {
	GapList<ChunkVBO> VBOs;
	ShaderProgram shader;

	
	
	public void addVBO(int vertexid,  int colorid, int normalid, int visibleBlocks){
		VBOs.add(new ChunkVBO(vertexid, colorid, normalid , visibleBlocks));		
	}
	
	
	public void draw(float x, float y, float z) {
        shader.bind();
        shader.setUniform("cameraPosition", x,y,z);      
       for(ChunkVBO c : VBOs){  
        	glBindBuffer(GL_ARRAY_BUFFER, c.vertexid);
    		glVertexPointer(3, GL_FLOAT, 0, 0L);
    		glBindBuffer(GL_ARRAY_BUFFER, c.colorid);
    		glColorPointer(3, GL_FLOAT, 0, 0L);
    		glBindBuffer(GL_ARRAY_BUFFER, c.normalid);
    		glNormalPointer(GL_FLOAT, 0, 0L);
    		glDrawArrays(GL_QUADS, 0, c.visibleFaces * 6);    		
        }
        ShaderProgram.unbind();		
	}
	
	//dispose the VAOs and VBOs
	public void dispose(){
	}
	
	
    
	public ChunkBatch(String vertexShader, String fragmentShader) {
		VBOs = new GapList<ChunkVBO>();
		// Create a new ShaderProgram
        shader = new ShaderProgram();
        //Attach the shaders
        shader.attachVertexShader(vertexShader);
        shader.attachFragmentShader(fragmentShader);
        
       
        shader.link();		
	}
   
    
    
  
	
}
