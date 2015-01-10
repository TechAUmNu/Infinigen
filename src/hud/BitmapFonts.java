package hud;
/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */



import de.matthiasmann.twl.utils.PNGDecoder;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import utility.BufferTools;
import utility.EulerCamera;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.*;

public class BitmapFonts {
	private static final String WINDOW_TITLE = "Infinigen";
	private static final int[] WINDOW_DIMENSIONS = { 1920, 1080 };
	private static final float ASPECT_RATIO = (float) WINDOW_DIMENSIONS[0]
			/ (float) WINDOW_DIMENSIONS[1];
    /** The string that is rendered on-screen. */
    private static final StringBuilder renderString = new StringBuilder("Enter your text");
    /** The texture object for the bitmap font. */
    private static int fontTexture;
    private static final EulerCamera camera = new EulerCamera.Builder()
	.setPosition(0f, 0f, 0f).setRotation(50, 12, 0)
	.setAspectRatio(ASPECT_RATIO).setFieldOfView(60)
	.setFarClippingPane(10000f).setNearClippingPane(0.1f).build();
  
    public static void main(String[] args) {
        setUpDisplay();
        
        setUpStates();
        setUpMatrices();
        enterGameLoop();
        cleanUp(false);
    }
    private static void setUpMatrices() {
		camera.applyPerspectiveMatrix();
	}
    private static void setUpDisplay() {
    	try {
			Display.setVSyncEnabled(false);
			Display.setFullscreen(true);
			Display.setResizable(false);
			Display.setTitle(WINDOW_TITLE);
			Display.create(new PixelFormat(4, 24, 0, 4));

		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp(true);
		}
    }

    private static void setUpTextures() throws IOException {
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

    private static void setUpStates() {
    	glShadeModel(GL_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_LIGHTING);
		//glEnable(GL_LIGHT0);
		glLightModel(GL_LIGHT_MODEL_AMBIENT,
				BufferTools.asFlippedFloatBuffer(new float[] { 1, 1f, 1f, 1f }));
		//glLight(GL_LIGHT0, GL_CONSTANT_ATTENUATION,
		//		BufferTools.asFlippedFloatBuffer(new float[] { 1, 1, 1, 1 }));

		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
		glMaterialf(GL_FRONT, GL_SHININESS, 50f);
		camera.applyOptimalStates();

		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		glEnable(GL_TEXTURE_2D);

		glClearColor(0.2f, 0.2f, 0.2f, 0f);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);

		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    private static void enterGameLoop() {
        while (!Display.isCloseRequested()) {
            render();
            input();
            update();
        }
    }

    private static void render() {
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	glLoadIdentity();
    	camera.applyTranslations();
    	make2D();
        
        
		renderString(renderString.toString(), fontTexture, 16, -0.9f, 0, 0.3f, 0.225f);
		make3D();
    }

    protected static void make2D() {
		// Remove the Z axis
		//GL11.glDisable(GL11.GL_LIGHTING);
		//GL11.glColor3f(0f, 0f, 0f);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		//GLU.gluOrtho2D(0, Display.getWidth(), 0,Display.getHeight());
		//GL11.glMatrixMode(GL11.GL_MODELVIEW);
		//GL11.glPushMatrix();
		GL11.glLoadIdentity();
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
                                     float characterWidth, float characterHeight) {
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

    private static void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                // Reset the string if we press escape.
                if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                    renderString.setLength(0);
                }
                // Append the pressed key to the string if the key isn't the back key or the shift key.
                if (Keyboard.getEventKey() != Keyboard.KEY_BACK) {
                    if (Keyboard.getEventKey() != Keyboard.KEY_LSHIFT) {
                        renderString.append(Keyboard.getEventCharacter());
                        //                        renderString.append((char) Keyboard.getEventCharacter() - 1);
                    }
                    // If the key is the back key, shorten the string by one character.
                } else if (renderString.length() > 0) {
                    renderString.setLength(renderString.length() - 1);
                }
            }
        }
    }

    private static void update() {
        Display.update();
        Display.sync(60);
    }

    private static void cleanUp(boolean asCrash) {
        glDeleteTextures(fontTexture);
        Display.destroy();
        System.exit(asCrash ? 1 : 0);
    }
}