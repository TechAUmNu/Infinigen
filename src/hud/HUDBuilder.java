package hud;

import static org.lwjgl.opengl.GL11.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.magicwerk.brownies.collections.GapList;

import utility.EulerCamera;
import de.matthiasmann.twl.utils.PNGDecoder;

public class HUDBuilder {
	private static int fontTexture;
	private GapList<ButtonText> buttons = new GapList<ButtonText>();
	
	public HUDBuilder() {

	}

	float rotation = 0.1f;
	int fps = 0;

	public void render(int fps, EulerCamera camera) {
		// Change to 2D so we can render the HUD
		makeText();
		// TODO:RENDER THE HUD

		drawText("FPS: " + fps, -1, 0.46f, 0.05f);
		drawText("MouseX: " + Mouse.getX(), -1, 0.44f, 0.05f);
		drawText("MouseY: " + Mouse.getY(), -1, 0.42f, 0.05f);
		drawText("CameraX: " + camera.x(), -1, 0.40f,0.05f);
		drawText("CameraY: " + camera.y(), -1, 0.38f,0.05f);
		drawText("CameraZ: " + camera.z(), -1, 0.36f,0.05f);
		

		
		//Change to orthographic view
		make2D();



		// Switch back to 3D
		make3D();
	}

	private void makeText() {
		
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		
	}

	void drawText(String s, float x, float y, float size){
		renderString(s, fontTexture, 16, x, y, size, size, 1);
	}
	
	void DrawButton(float start_angle, float arc_angle, int num_segments, String text, boolean left) {
		//Draws a button with the specified text
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
		
		//System.out.println(line2x);
		if(left){
			buttons.add(new ButtonText(text, convertResolutionX(line2x), convertResolutionY(line2y)));
		}else{
			buttons.add(new ButtonText(text, convertResolutionX(line1x2) , convertResolutionY(line1y2)+ 0.03f));
		}
	}

	
	protected static void make2D() {
		// Remove the Z axis
		// glDisable(GL_LIGHTING);
		// glColor3f(0f, 0f, 0f);

		GLU.gluOrtho2D(0, Display.getWidth(), 0, Display.getHeight());

	}

	private float convertResolutionY(float y) {
		float pos = (y / (Display.getHeight()) -0.5f);
		System.out.println(pos);
		return pos;
	}

	private float convertResolutionX(float x){
		float pos = (x / (Display.getWidth() / 2f) ) - 1f;
		System.out.println(pos);
		return pos;
	}
	
	public static void setUpTextures() throws IOException {
		// Create a new texture for the bitmap font.
		fontTexture = glGenTextures();
		// Bind the texture object to the GL_TEXTURE_2D target, specifying that
		// it will be a 2D texture.
		glBindTexture(GL_TEXTURE_2D, fontTexture);
		// Use TWL's utility classes to load the png file.
		PNGDecoder decoder = new PNGDecoder(new FileInputStream(
				"res/Fonts/font.png"));
		ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth()
				* decoder.getHeight());
		decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
		buffer.flip();
		// Load the previously loaded texture data into the texture object.
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, decoder.getWidth(),
				decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		// Unbind the texture.
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	protected static void make3D() {
		// Restore the Z axis
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();

		// glEnable(GL_DEPTH_TEST);
		// glEnable(GL_LIGHTING);
	}

	/**
	 * Renders text using a font bitmap.
	 *
	 * @param string
	 *            the string to render
	 * @param textureObject
	 *            the texture object containing the font glyphs
	 * @param gridSize
	 *            the dimensions of the bitmap grid (e.g. 16 -> 16x16 grid; 8 ->
	 *            8x8 grid)
	 * @param x
	 *            the x-coordinate of the bottom-left corner of where the string
	 *            starts rendering
	 * @param y
	 *            the y-coordinate of the bottom-left corner of where the string
	 *            starts rendering
	 * @param characterWidth
	 *            the width of the character
	 * @param characterHeight
	 *            the height of the character
	 */
	private static void renderString(String string, int textureObject,
			int gridSize, float x, float y, float characterWidth,
			float characterHeight, float scale) {
		glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
		glEnable(GL_CULL_FACE);
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, textureObject);
		// Enable linear texture filtering for smoothed results.
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		// Enable additive blending. This means that the colours will be added
		// to already existing colours in the
		// frame buffer. In practice, this makes the black parts of the texture
		// become invisible.
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		// Store the current model-view matrix.
		glPushMatrix();
		// Offset all subsequent (at least up until 'glPopMatrix') vertex
		// coordinates.
		glTranslatef(x, y, 0);
		glScalef(scale, scale, scale);
		glBegin(GL_QUADS);
		// Iterate over all the characters in the string.
		for (int i = 0; i < string.length(); i++) {
			// Get the ASCII-code of the character by type-casting to integer.
			int asciiCode = (int) string.charAt(i);
			// There are 16 cells in a texture, and a texture coordinate ranges
			// from 0.0 to 1.0.
			final float cellSize = 1.0f / gridSize;
			// The cell's x-coordinate is the greatest integer smaller than
			// remainder of the ASCII-code divided by the
			// amount of cells on the x-axis, times the cell size.
			float cellX = ((int) asciiCode % gridSize) * cellSize;
			// The cell's y-coordinate is the greatest integer smaller than the
			// ASCII-code divided by the amount of
			// cells on the y-axis.
			float cellY = ((int) asciiCode / gridSize) * cellSize;
			glTexCoord2f(cellX, cellY + cellSize);
			glVertex2f(i * characterWidth / 3, y);
			glTexCoord2f(cellX + cellSize, cellY + cellSize);
			glVertex2f(i * characterWidth / 3 + characterWidth / 2, y);
			glTexCoord2f(cellX + cellSize, cellY);
			glVertex2f(i * characterWidth / 3 + characterWidth / 2, y
					+ characterHeight);
			glTexCoord2f(cellX, cellY);
			glVertex2f(i * characterWidth / 3, y + characterHeight);
		}
		glEnd();
		glPopMatrix();
		glPopAttrib();
	}

}
