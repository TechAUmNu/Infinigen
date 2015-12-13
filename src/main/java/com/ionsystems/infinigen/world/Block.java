package main.java.com.ionsystems.infinigen.world;

import java.io.Serializable;

public class Block implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3524884246455631273L;
	BlockType type;
	boolean visible, top, bottom, left, right, front, back;
	public float weight;

	public Block(BlockType type) {
		this.type = type;
		visible = false;
	}

	public byte GetType() {
		return type.GetType();
	}

	public void SetVisible(boolean b) {
		visible = b;
	}

	public BlockType getType() {
		return type;
	}

	public void setType(BlockType type) {
		this.type = type;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isTopVisible() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public boolean isBottomVisible() {
		return bottom;
	}

	public void setBottom(boolean bottom) {
		this.bottom = bottom;
	}

	public boolean isLeftVisible() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRightVisible() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isFrontVisible() {
		return front;
	}

	public void setFront(boolean front) {
		this.front = front;
	}

	public boolean isBackVisible() {
		return back;
	}

	public void setBack(boolean back) {
		this.back = back;
	}

}
