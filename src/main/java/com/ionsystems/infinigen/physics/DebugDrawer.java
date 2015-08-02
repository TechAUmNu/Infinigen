package main.java.com.ionsystems.infinigen.physics;

import javax.vecmath.Vector3f;

import main.java.com.ionsystems.infinigen.global.Globals;
import main.java.com.ionsystems.infinigen.models.RawModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;

public class DebugDrawer extends IDebugDraw{

	PhysicsDebugShader shader;
	
	public DebugDrawer() {
		shader = new PhysicsDebugShader();		
	}

	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		
		//System.out.println("Drawing Line from" + from + to);
		
		//Convert the points to positions array
		
		float[] positions = new float[6];
		positions[0] = from.x;
		positions[1] = from.y;
		positions[2] = from.z;
		
		positions[3] = to.x;
		positions[4] = to.y;
		positions[5] = to.z;
		
		
		
		
		//Create a rawmodel to store the line
		RawModel model = Globals.getLoader().loadToVAO(positions, 3);
		
		
		shader.start();	
		shader.loadProjectionMatrix(Globals.getRenderer().getProjectionMatrix());
		shader.loadViewMatrix(Globals.getActiveCamera());
		GL30.glBindVertexArray(model.getVaoID());
		  
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_LINES, 0, 6);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
		model.cleanUp();
		
	}

	@Override
	public void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportErrorWarning(String warningString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw3dText(Vector3f location, String textString) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDebugMode(int debugMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDebugMode() {
		// TODO Auto-generated method stub
		return DebugDrawModes.DRAW_WIREFRAME;
	}

}
