package oldworld;

public class Block {

	private boolean IsVisible;
	private BlockType Type;
	private VisibleFaces vf;

	public enum BlockType {
		BlockType_Air((byte) 0), BlockType_Dirt((byte) 1), BlockType_Stone((byte) 2);
		private byte BlockID;

		BlockType(byte i) {
			BlockID = i;
		}

		public byte GetType() {
			return BlockID;
		}

		public static BlockType fromByte(byte b) {
			switch (b) {
			case (byte) 0:
				return BlockType_Air;
			case (byte) 1:
				return BlockType_Dirt;
			case (byte) 2:
				return BlockType_Stone;
			}
			return null;
		}
	}

	public Block(BlockType type) {
		Type = type;
	}

	public boolean IsVisible() {
		return IsVisible;
	}

	public void SetVisible(boolean visible) {
		IsVisible = visible;
	}

	public byte GetType() {
		return Type.GetType();
	}

	public void setType(BlockType b) {
		Type = b;
	}

	public VisibleFaces getVf() {
		return vf;
	}

	public void setVf(VisibleFaces vf) {
		this.vf = vf;
	}

}
