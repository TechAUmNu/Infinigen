package world;

public class Block {
	private boolean IsActive;
	private BlockType Type;

	public enum BlockType {
		BlockType_Grass(0), BlockType_Dirt(1), BlockType_Water(
				2), BlockType_Stone(3), BlockType_Wood(4), BlockType_Sand(5);
		private int BlockID;

		BlockType(int i) {
			BlockID = i;
		}

		public int GetID() {
			return BlockID;
		}
	}

	public Block(BlockType type) {
		Type = type;
	}

	public boolean IsActive() {
		return IsActive;
	}

	public void SetActive(boolean active) {
		IsActive = active;
	}

	public int GetID() {
		return Type.GetID();
	}
}
