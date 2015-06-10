package oldgraphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL13.*;
import oldshaders.Shader;
import oldshaders.ShaderManager;
import oldutility.ShaderProgram;

import org.magicwerk.brownies.collections.GapList;
import org.newdawn.slick.opengl.Texture;

// A batch is a group of objects that need to be rendered. 
// The job of the batch is to allow all the triangles to be drawn using as few draw calls as possible. 
// Therefore reducing the load on the cpu.

// A batch is specific to a shader.
public class ChunkBatch {
	GapList<ChunkVBO> VBOs;
	//Shader shader;

	public void addVBO(int vertexid, int colorid, int normalid,
			int visibleBlocks, int textureid) {
		VBOs.add(new ChunkVBO(vertexid, colorid, normalid, visibleBlocks, textureid));
	}

	public void draw(float x, float y, float z, Texture textureHandle) {
		//ShaderProgram s = ShaderManager.getInstance().getShaderProgram(shader);
		//s.bind();
		//s.setUniform("cameraPosition", x, y, z);
		
		
		
		
		for (ChunkVBO c : VBOs) {
			
			/////////////////////////////////////////////////////////////////
			// Clear/Initialize the display
			//
			// Clear the color so the previous color won't be used to override 
			// anything that you didn't have a color for.
			glColor3f(1.0f, 1.0f, 1.0f);
			
			// Disable the use of textures so that the previous texture's
			// properties are not used to color the current object that
			// doesn't have a texture.
			glDisable(GL_TEXTURE_2D);
			
			// Same as above, disable the color material until it is used
			glDisable(GL_COLOR_MATERIAL);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_NORMAL_ARRAY);
			glEnable(GL_TEXTURE_2D);
			
			// Enable linear texture filtering for smoothed results.
			//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			textureHandle.bind();		
			
			
			
			glBindBuffer(GL_ARRAY_BUFFER, c.vertexid);
			glVertexPointer(3, GL_FLOAT, 0, 0L);
			//glBindBuffer(GL_ARRAY_BUFFER, c.colorid);
			//glColorPointer(3, GL_FLOAT, 0, 0L);			
			glBindBuffer(GL_ARRAY_BUFFER, c.normalid);
			glNormalPointer(GL_FLOAT, 0, 0L);
			glBindBuffer(GL_ARRAY_BUFFER, c.textureid);
			glTexCoordPointer(2, GL_FLOAT, 0, 0L);
			
			
			glDrawArrays(GL_QUADS, 0, c.visibleFaces * 6);
			
			
		    glBindBuffer(GL_ARRAY_BUFFER, 0);
		    
		    
		    
		    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		    
		}
		//ShaderProgram.unbind();
	}

	// dispose the VAOs and VBOs
	public void dispose() {
	}

	public ChunkBatch(Shader s) {
		VBOs = new GapList<ChunkVBO>();
		//shader = s;
	}

}
