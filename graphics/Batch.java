package graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.magicwerk.brownies.collections.GapList;

import utility.ShaderProgram;


// A batch is a group of objects that need to be rendered. 
// The job of the batch is to allow all the triangles to be drawn using as few draw calls as possible. 
// Therefore reducing the load on the cpu.

// A batch is specific to a shader.
public class Batch {
	GapList<VBO> VBOs;
	ShaderProgram shader;

	
	
	public void addVBO(int vboID, int noVertices){
		VBOs.add(new VBO(vboID, noVertices));
		print();
	}
	public void print(){
		int i = 0;
		for(VBO vao : VBOs){
			i = i + vao.noVertices;
		}
		System.out.println(i);
	}
	
	public void draw(float x, float y, float z) {
		
		// Bind our ShaderProgram
        shader.bind();
        shader.setUniform("cameraPosition", x,y,z);
      
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, VBOs.get(0).id);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOs.get(1).id);
        glNormalPointer(GL_FLOAT, 0, 0L);
		        
        //Draw each of the VAOs       
       
        //for(VBO vbo : VBOs){
        	
        	// Bind the VBO
            //glBindVertexArray(vbo.id);
            
            // Enable the location 0 to send vertices to the shader
            //glEnableVertexAttribArray(0);
        	//shader.setUniform("TessLevelInner", 3);
        	//shader.setUniform("TessLevelOuter", 3);
        	
        	//glPatchParameteri(GL_PATCH_VERTICES, 3);
        	//glDrawElements(GL_PATCHES, IndexCount, GL_UNSIGNED_INT, 0);
            glDrawArrays(GL_TRIANGLES, 0,  VBOs.get(0).noVertices);
            // Disable the location 0 and unbind the VAO
            //glDisableVertexAttribArray(0);       
            //glBindVertexArray(0);
        //} 
        
        // Unbind the ShaderProgram
        ShaderProgram.unbind();
		
	}
	
	//dispose the VAOs and VBOs
	public void dispose(){
//		for(VBO vbo : VBOs){
//			// Dispose the VAO
//	        glBindVertexArray(0);
//	        glDeleteVertexArrays(vbo.id);
//	        
//	        // Dispose the VBO
//	        glBindBuffer(GL_ARRAY_BUFFER, 0);
//	        glDeleteBuffers(vbo.vboID);
//		}
	}
	
	
    
	public Batch(String vertexShader, String fragmentShader) {
		VBOs = new GapList<VBO>();
		// Create a new ShaderProgram
        shader = new ShaderProgram();
        //Attach the shaders
        shader.attachVertexShader(vertexShader);
        shader.attachFragmentShader(fragmentShader);
        
       
        shader.link();		
	}
   
    
    
  
	
}
