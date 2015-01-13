package hud;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_ENABLE_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;




import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import utility.EulerCamera;
import de.matthiasmann.twl.utils.PNGDecoder;



public class HUDBuilder {
	private static int fontTexture;
	
	public HUDBuilder(){
		
	}
	
	
	
	
	float rotation = 0.1f;
	int fps = 0;
	public void render(int fps, EulerCamera camera) {
		// Change to 2D so we can render the HUD
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		// TODO:RENDER THE HUD
		
		renderString("Test", fontTexture, 16, -1, 0, 0.1f, 0.1f, 1);
		
		make2D();
		rotation += 0.4f;
		//glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 0);
		//glRotatef(rotation, 0f, 0f, 1f);
	//glTranslatef(-Display.getWidth() / 2, -Display.getHeight() / 2, 0);

		
		DrawButton(-0.4f, 0.1f, 10);
		DrawButton(-0.28f, 0.1f, 10);
		DrawButton(-0.16f, 0.1f, 10);
		DrawButton(-0.04f, 0.1f, 10);
		DrawButton(0.08f, 0.1f, 10);
		DrawButton(0.20f, 0.1f, 10);
		DrawButton(0.32f, 0.1f, 10);		
		
		
		DrawButton(-3.55f, 0.1f, 10);
		DrawButton(-3.43f, 0.1f, 10);
		DrawButton(-3.31f, 0.1f, 10);
		DrawButton(-3.19f, 0.1f, 10);
		DrawButton(-3.07f, 0.1f, 10);
		DrawButton(-2.95f, 0.1f, 10);
		DrawButton(-2.83f, 0.1f, 10);		
		
		// Switch back to 3D
		
		make3D();
	}
	

	void DrawButton(float start_angle, float arc_angle, int num_segments) {
		float theta = arc_angle / (float) (num_segments - 1);// theta is now
																// calculated
																// from the arc
																// angle
																// instead, the
																// - 1 bit comes
																// from the fact
																// that the arc
																// is open

		float tangetial_factor = (float) Math.tan(theta);

		float radial_factor = (float) Math.cos(theta);

		float x = (float) ((float) (Display.getHeight() / 1.2) * Math
				.cos(start_angle));// we now start at the start angle
		float y = (float) ((float) (Display.getHeight() / 1.2) * Math
				.sin(start_angle));

		float x2 = (float) ((float) (Display.getHeight() / 1.3) * Math
				.cos(start_angle));// we now start at the start angle
		float y2 = (float) ((float) (Display.getHeight() / 1.3) * Math
				.sin(start_angle));

		float line1x = 0, line1y = 0, line1x2 = 0, line1y2 = 0;
		float line2x = 0, line2y = 0, line2x2 = 0, line2y2 = 0;

		glBegin(GL_LINE_STRIP);// since the arc is not a closed curve, this is a
								// strip now
		for (int i = 0; i < num_segments; i++) {

			glVertex2f(x + Display.getWidth() / 2, y + Display.getHeight() / 2);
			if (i == 0) {
				line1x = x + Display.getWidth() / 2;
				line1y = y + Display.getHeight() / 2;
			}
			if (i == num_segments - 1) {
				line2x = x + Display.getWidth() / 2;
				line2y = y + Display.getHeight() / 2;
			}
			float tx = -y;
			float ty = x;

			x += tx * tangetial_factor;
			y += ty * tangetial_factor;

			x *= radial_factor;
			y *= radial_factor;
		}
		glEnd();
		glBegin(GL_LINE_STRIP);// since the arc is not a closed curve, this is a
								// strip now
		for (int i = 0; i < num_segments; i++) {

			glVertex2f(x2 + Display.getWidth() / 2, y2 + Display.getHeight()
					/ 2);
			if (i == 0) {
				line1x2 = x2 + Display.getWidth() / 2;
				line1y2 = y2 + Display.getHeight() / 2;
			}
			if (i == num_segments - 1) {
				line2x2 = x2 + Display.getWidth() / 2;
				line2y2 = y2 + Display.getHeight() / 2;
			}
			float tx = -y2;
			float ty = x2;

			x2 += tx * tangetial_factor;
			y2 += ty * tangetial_factor;

			x2 *= radial_factor;
			y2 *= radial_factor;
		}
		glEnd();
		glBegin(GL_LINE_STRIP);

		glVertex2f(line1x, line1y);
		glVertex2f(line1x2, line1y2);
		glEnd();
		glBegin(GL_LINE_STRIP);
		glVertex2f(line2x, line2y);
		glVertex2f(line2x2, line2y2);
		glEnd();
	}

	protected static void make2D() {
		// Remove the Z axis
		//GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glColor3f(0f, 0f, 0f);
		
		GLU.gluOrtho2D(0, Display.getWidth(), 0,Display.getHeight());
		
		
	}

	
	public static void setUpTextures() throws IOException {
        // Create a new texture for the bitmap font.
        fontTexture = glGenTextures();
        // Bind the texture object to the GL_TEXTURE_2D target, specifying that it will be a 2D texture.
        glBindTexture(GL_TEXTURE_2D, fontTexture);
        // Use TWL's utility classes to load the png file.
        PNGDecoder decoder = new PNGDecoder(new FileInputStream("res/Fonts/font.png"));
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buffer.flip();
        // Load the previously loaded texture data into the texture object.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
                buffer);
        // Unbind the texture.
        glBindTexture(GL_TEXTURE_2D, 0);
    }
	
	protected static void make3D() {
		// Restore the Z axis
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);		
		GL11.glPopMatrix();
		
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glEnable(GL11.GL_LIGHTING);
	}
	/**
     * Renders text using a font bitmap.
     *
     * @param string the string to render
     * @param textureObject the texture object containing the font glyphs
     * @param gridSize the dimensions of the bitmap grid (e.g. 16 -> 16x16 grid; 8 -> 8x8 grid)
     * @param x the x-coordinate of the bottom-left corner of where the string starts rendering
     * @param y the y-coordinate of the bottom-left corner of where the string starts rendering
     * @param characterWidth the width of the character
     * @param characterHeight the height of the character
     */
    private static void renderString(String string, int textureObject, int gridSize, float x, float y,
                                     float characterWidth, float characterHeight, float scale) {
        glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureObject);
        // Enable linear texture filtering for smoothed results.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // Enable additive blending. This means that the colours will be added to already existing colours in the
        // frame buffer. In practice, this makes the black parts of the texture become invisible.
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        // Store the current model-view matrix.
        glPushMatrix();
        // Offset all subsequent (at least up until 'glPopMatrix') vertex coordinates.
        glTranslatef(x, y, 0);
        GL11.glScalef(scale,scale,scale);
        glBegin(GL_QUADS);
        // Iterate over all the characters in the string.
        for (int i = 0; i < string.length(); i++) {
            // Get the ASCII-code of the character by type-casting to integer.
            int asciiCode = (int) string.charAt(i);
            // There are 16 cells in a texture, and a texture coordinate ranges from 0.0 to 1.0.
            final float cellSize = 1.0f / gridSize;
            // The cell's x-coordinate is the greatest integer smaller than remainder of the ASCII-code divided by the
            // amount of cells on the x-axis, times the cell size.
            float cellX = ((int) asciiCode % gridSize) * cellSize;
            // The cell's y-coordinate is the greatest integer smaller than the ASCII-code divided by the amount of
            // cells on the y-axis.
            float cellY = ((int) asciiCode / gridSize) * cellSize;
            glTexCoord2f(cellX, cellY + cellSize);
            glVertex2f(i * characterWidth / 3, y);
            glTexCoord2f(cellX + cellSize, cellY + cellSize);
            glVertex2f(i * characterWidth / 3 + characterWidth / 2, y);
            glTexCoord2f(cellX + cellSize, cellY);
            glVertex2f(i * characterWidth / 3 + characterWidth / 2, y + characterHeight);
            glTexCoord2f(cellX, cellY);
            glVertex2f(i * characterWidth / 3, y + characterHeight);
        }
        glEnd();
        glPopMatrix();
        glPopAttrib();
    }

}
