package hud;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import hud.FontRender.GLFont;

import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;



public class HUDBuilder {
	
	
	public HUDBuilder(){
		init();
	}
	
	
	private GLFont font;

	public void init() {
		try {
			//font = FontRender.createFont("Dialog", 11, false, true);
			font = new GLFont(HUDBuilder.class.getResourceAsStream("DejaVu_Sans_11.fnt"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	float rotation = 0.1f;
	int fps = 0;
	public void render(int fps) {
		// Change to 2D so we can render the HUD
		make2D();
		// TODO:RENDER THE HUD
		
	    
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
		for(int i = 0; i < 10000 ; i = i + 10){
		DrawText("Hello Test ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd", i, i, 1 ,1, 1);
		
		}
		
		// Switch back to 3D
		
		make3D();
	}
	void DrawText(CharSequence text, int x, int y, float red, float green, float blue){
		FontRender.drawString(font, text, x, y, red, green, blue);
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

		float x = (float) ((float) (Display.getHeight() / 1.1) * Math
				.cos(start_angle));// we now start at the start angle
		float y = (float) ((float) (Display.getHeight() / 1.1) * Math
				.sin(start_angle));

		float x2 = (float) ((float) (Display.getHeight() / 1.2) * Math
				.cos(start_angle));// we now start at the start angle
		float y2 = (float) ((float) (Display.getHeight() / 1.2) * Math
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
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0, Display.getWidth(), Display.getHeight(),0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
	}

	
	
	
	protected static void make3D() {
		// Restore the Z axis
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);		
		GL11.glPopMatrix();
		
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glEnable(GL11.GL_LIGHTING);
	}
	
}
