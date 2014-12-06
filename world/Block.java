package world;

public class Block {
	
	private boolean IsVisible;
	private BlockType Type;

	public enum BlockType {
		BlockType_Air(0), BlockType_Dirt(1), BlockType_Stone(2);
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

	public boolean IsVisible() {
		return IsVisible;
	}

	public void SetVisible(boolean visible) {
		IsVisible = visible;
	}

	public int GetID() {
		return Type.GetID();
	}
}
