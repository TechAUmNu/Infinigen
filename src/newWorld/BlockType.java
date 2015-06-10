package newWorld;

/**
 * Class for the type of block this is.
 * @author Euan
 *
 */
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
