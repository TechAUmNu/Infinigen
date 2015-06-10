package oldphysics;

import java.nio.FloatBuffer;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import static org.lwjgl.opengl.GL11.*;

public class PhysicsObject {
	public RigidBody body;
	
	public Vector3f getPosition(){
		return body.getWorldTransform(new Transform()).origin;
	}
		
	public void translateToPosition(){
		// These can be pre-allocated.
		float[] matrix = new float[16];
		Transform transform = new Transform();
		FloatBuffer transformationBuffer = BufferUtils.createFloatBuffer(16);

		// Get the transformation matrix from JBullet.
	
		MotionState motionState = body.getMotionState();
		motionState.getWorldTransform(transform);
		transform.getOpenGLMatrix(matrix);

		// Put the transformation matrix into a FloatBuffer.
		transformationBuffer.clear();
		transformationBuffer.put(matrix);
		transformationBuffer.flip();

		
		glMultMatrix(transformationBuffer); // Apply the object transformation 
		
	}
	
	
	
}
