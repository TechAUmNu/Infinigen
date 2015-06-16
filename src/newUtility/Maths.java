package newUtility;

import javax.vecmath.Quat4f;

import newEntities.Camera;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Maths {

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static float convertToHeading(Quat4f quat4f) {
		double heading, attitude, bank;
		double test = quat4f.x * quat4f.y + quat4f.z * quat4f.w;
		if (test > 0.499) { // singularity at north pole
			heading = 2 * Math.atan2(quat4f.x, quat4f.w);
			attitude = Math.PI / 2;
			bank = 0;
			return (float) heading;
		}
		if (test < -0.499) { // singularity at south pole
			heading = -2 * Math.atan2(quat4f.x, quat4f.w);
			attitude = -Math.PI / 2;
			bank = 0;
			return (float) heading;
		}
		double sqx = quat4f.x * quat4f.x;
		double sqy = quat4f.y * quat4f.y;
		double sqz = quat4f.z * quat4f.z;
		heading = Math.atan2(2 * quat4f.y * quat4f.w - 2 * quat4f.x * quat4f.z, 1 - 2 * sqy - 2 * sqz);
		attitude = Math.asin(2 * test);
		bank = Math.atan2(2 * quat4f.x * quat4f.w - 2 * quat4f.y * quat4f.z, 1 - 2 * sqx - 2 * sqz);
		float rotationDegrees = (float) Math.toDegrees(heading);
		return rotationDegrees;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float rw, float scale, boolean inRadians) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);

		if (inRadians) {
			Matrix4f.rotate((float) (rx * rw), new Vector3f(1, 0, 0), matrix, matrix);
			Matrix4f.rotate((float) (ry * rw), new Vector3f(0, 1, 0), matrix, matrix);
			Matrix4f.rotate((float) (rz * rw), new Vector3f(0, 0, 1), matrix, matrix);

		} else {
			Matrix4f.rotate((float) Math.toRadians(rx * rw), new Vector3f(1, 0, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(ry * rw), new Vector3f(0, 1, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(rz * rw), new Vector3f(0, 0, 1), matrix, matrix);
		}

		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	public static org.lwjgl.util.vector.Matrix4f convertMatrix(javax.vecmath.Matrix4f matrix) {

		Matrix4f outMatrix = new Matrix4f();
		outMatrix.m00 = matrix.m00;
		outMatrix.m01 = matrix.m01;
		outMatrix.m02 = matrix.m02;
		outMatrix.m03 = matrix.m03;
		outMatrix.m10 = matrix.m10;
		outMatrix.m11 = matrix.m11;
		outMatrix.m12 = matrix.m12;
		outMatrix.m13 = matrix.m13;
		outMatrix.m20 = matrix.m20;
		outMatrix.m21 = matrix.m21;
		outMatrix.m22 = matrix.m22;
		outMatrix.m23 = matrix.m23;
		return outMatrix;
	}

	public static Vector2f convertCoordinate(Vector2f coord) {
		coord.x /= 1920;
		coord.y /= 1080;
		coord.x *= Display.getWidth();
		coord.y *= Display.getHeight();
		return coord;
	}
	
	public static javax.vecmath.Vector3f convertVector(Vector3f vector){
		javax.vecmath.Vector3f newVector = new javax.vecmath.Vector3f();
		newVector.x = vector.x;
		newVector.y = vector.y;
		newVector.z = vector.z;
		return newVector;
		
	}
	
	
	public static Vector3f convertVectorBtoL(javax.vecmath.Vector3f vector){
		Vector3f newVector = new Vector3f();
		newVector.x = vector.x;
		newVector.y = vector.y;
		newVector.z = vector.z;
		return newVector;
		
	}
}
