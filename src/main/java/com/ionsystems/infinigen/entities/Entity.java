package main.java.com.ionsystems.infinigen.entities;

import java.io.Serializable;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import main.java.com.ionsystems.infinigen.models.TexturedModel;
import main.java.com.ionsystems.infinigen.utility.Maths;

public class Entity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 134868214896741001L;
	protected TexturedModel model;
	protected Vector3f position;
	protected float rotX, rotY, rotZ;

	protected float scale;
	protected Matrix4f transformationMatrix;

	private int textureIndex = 0;

	public Entity() {

	}

	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.textureIndex = index;
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public float getTextureXOffset() {
		int column = textureIndex % model.getTexture().getNumberOfRows();
		return (float) column / (float) model.getTexture().getNumberOfRows();
	}

	public float getTextureYOffset() {
		int row = textureIndex / model.getTexture().getNumberOfRows();
		return (float) row / (float) model.getTexture().getNumberOfRows();
	}

	public void increasePosition(float dx, float dy, float dz) {
		position.x += dx;
		position.y += dy;
		position.z += dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		rotX += dx;
		rotY += dy;
		rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {

		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}

	public void setTransformationMatrix(Matrix4f transformationMatrix) {
		this.transformationMatrix = transformationMatrix;
	}

	public Matrix4f updateTransformationMatrix() {
		return Maths.createTransformationMatrix(getPosition(), getRotX(), getRotY(), getRotZ(), 1, getScale(), false);
	}

	public float getSizeX() {
		return model.getPhysicsModel().getSizeX();
	}

	public void setSizeX(float sizeX) {
		model.getPhysicsModel().setSizeX(sizeX);
	}

	public float getSizeY() {
		return model.getPhysicsModel().getSizeY();
	}

	public void setSizeY(float sizeY) {
		model.getPhysicsModel().setSizeY(sizeY);
	}

	public float getSizeZ() {
		return model.getPhysicsModel().getSizeZ();
	}

	public void setSizeZ(float sizeZ) {
		model.getPhysicsModel().setSizeX(sizeZ);
	}

}
