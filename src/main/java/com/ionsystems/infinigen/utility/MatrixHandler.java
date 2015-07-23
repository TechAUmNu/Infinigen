package main.java.com.ionsystems.infinigen.utility;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MatrixHandler extends Matrix4f {
	private static final long serialVersionUID = 1L;
	
	private Vector3f position;
	private Vector3f angle;
	private Vector3f scale;
	
	public MatrixHandler() {
		super();
		position = new Vector3f(0, 0, 0);
		angle = new Vector3f(0, 0, 0);
		scale = new Vector3f(1, 1, 1);
	}
	
	public void updateMatrix() {
		this.setIdentity();
		
		Matrix4f.scale(scale, this, this);
		Matrix4f.translate(position, this, this);
		Matrix4f.rotate((float)Math.toRadians(angle.z), new Vector3f(0, 0, 1), 
				this, this);
		Matrix4f.rotate((float)Math.toRadians(angle.y), new Vector3f(0, 1, 0), 
				this, this);
		Matrix4f.rotate((float)Math.toRadians(angle.x), new Vector3f(1, 0, 0), 
				this, this);
	}
	
	public void initPerspectiveMatrix() {
		float fieldOfView = 60f;
		float aspectRatio = (float)Display.getWidth() / (float)Display.getHeight();
		float zNear = 0.1f;
		float zFar = 100f;

		initPerspectiveMatrix(fieldOfView, aspectRatio, zNear, zFar);
	}
	
	public void initPerspectiveMatrix(float fieldOfView, float aspectRatio, float zNear, float zFar) {
		float yScale = 1.0f / ((float) Math.tan(Math.toRadians(fieldOfView / 2.0f)));
		float xScale = yScale / aspectRatio;
		float depth = zFar - zNear;
		
		this.setIdentity();

		this.m00 = xScale;
		this.m11 = yScale;
		this.m22 = -((zFar + zNear) / depth);
		this.m23 = -1;
		this.m32 = -((2 * zNear * zFar) / depth);
		this.m33 = 0;
	}
	
	public void initOrthographicMatrix() {
		float left = -1;
		float right = 1;
		float bottom = -1;
		float top = 1;
		float near = 1;
		float far = -1;

		initOrthographicMatrix(left, right, bottom, top, near, far);
	}
	
	public void initOrthographicMatrix(float left, float right, float bottom, float top, float near, float far) {
		float width = right - left;
		float height = top - bottom;
		float depth = far - near;
		
		this.setIdentity();
		
		this.m00 = 2.0f / width;
		this.m11 = 2.0f / height;
		this.m22 = -2.0f / depth;
		this.m30 = -(right + left) / (right - left);
		this.m31 = -(top + bottom) / (top - bottom);
		this.m32 = -(far + near) / (far - near);
		this.m33 = 1.0f;
	}
	
	public void lookAt(Vector3f position, Vector3f direction, Vector3f up) {
		
		Vector3f f = new Vector3f();
		Vector3f u = new Vector3f();
		Vector3f s = new Vector3f();
		Vector3f.sub(direction, position, f);
		f.normalise(f);
		up.normalise(u);
		Vector3f.cross(f, u, s);
		s.normalise(s);
		Vector3f.cross(s, f, u);

	    this.setIdentity();
	    this.m00 = s.x;
	    this.m10 = s.y;
	    this.m20 = s.z;
	    this.m01 = u.x;
	    this.m11 = u.y;
	    this.m21 = u.z;
	    this.m02 = -f.x;
	    this.m12 = -f.y;
	    this.m22 = -f.z;
	    this.m30 = -Vector3f.dot(s, position);
	    this.m31 = -Vector3f.dot(u, position);
	    this.m32 = Vector3f.dot(f, position);
	}
	
	public void setBias() {
	    this.setIdentity();
	    this.m00 = 0.5f;
	    this.m11 = 0.5f;
	    this.m22 = 0.5f;
	    this.m30 = 0.5f;
	    this.m31 = 0.5f;
	    this.m32 = 0.5f;
	    this.m33 = 1.0f;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		updateMatrix();
	}

	public Vector3f getAngle() {
		return angle;
	}

	public void setAngle(Vector3f angle) {
		this.angle = angle;
		updateMatrix();
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
		updateMatrix();
	}
}
