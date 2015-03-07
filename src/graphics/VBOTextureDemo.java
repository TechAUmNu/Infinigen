package graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class VBOTextureDemo {

private static Texture texture;

public static void main(String[] args) {
    try {
        Display.setDisplayMode(new DisplayMode(500, 500));
        Display.setTitle("Texture");
        Display.create();
    } catch (LWJGLException e) {
        e.printStackTrace();
        Display.destroy();
        System.exit(1);
    }

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(1, -1, 1, -1, 1, 1);
    glMatrixMode(GL_MODELVIEW);
    glEnable(GL_TEXTURE_2D);
    glLoadIdentity();


    try {
        texture = TextureLoader.getTexture("PNG",
                ResourceLoader.getResourceAsStream("res/images/uvgrid01.png"));
    } catch (IOException e) {
        e.printStackTrace();
    }

    final int amountOfVertices = 6;
    final int vertexSize = 3;
    final int texSize = 2;

    FloatBuffer vertexData = BufferUtils.createFloatBuffer(amountOfVertices
            * vertexSize);
    vertexData.put(new float[] { -10f, 10f, 0f, }); // Vertex
    vertexData.put(new float[] { 10f, 10f, 0f, }); // Vertex
    vertexData.put(new float[] { -10f, -10f, 0f, }); // Vertex

    vertexData.put(new float[] { 10f, -10f, 0f, }); // Vertex
    vertexData.put(new float[] { -10f, -10f, 0f, }); // Vertex
    vertexData.put(new float[] { 10f, 10f, 0f, }); // Vertex;
    vertexData.flip();

    FloatBuffer textureData = BufferUtils
            .createFloatBuffer(amountOfVertices * texSize);
    textureData.put(new float[] { 0f, 1f,1f, 1f,0f, 0f,1f, 0f, 0f, 0f,1f, 1f, }); // Texture Coordinate
    textureData.flip();

    //glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

   // texture.bind();

    int vboVertexHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
    glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);


    int vboTexCoordHandle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboTexCoordHandle);
    glBufferData(GL_ARRAY_BUFFER, textureData, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);

    glClearColor(0.5f, 0.1f, 0f, 1f);



    while (!Display.isCloseRequested()) {
        glClear(GL_COLOR_BUFFER_BIT);

        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(vertexSize, GL_FLOAT, 0, 0L);

        glBindBuffer(GL_ARRAY_BUFFER, vboTexCoordHandle);
        glTexCoordPointer(2, GL_FLOAT, 0, 0);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glDrawArrays(GL_TRIANGLES, 0, amountOfVertices);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);

        Display.update();
        Display.sync(60);
    }

    glDeleteBuffers(vboVertexHandle);
    glDeleteBuffers(vboTexCoordHandle);
   // texture.release();


    Display.destroy();
    System.exit(0);
}
}