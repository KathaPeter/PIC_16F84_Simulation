package memory.guiutil;

public class Value_Pair<T> {

	private String name;
	private T value;
	private boolean bBits;

	public Value_Pair(String name, T value) {
		super();
		this.name = name;
		this.value = value;
		this.bBits = true;
	}

	public Value_Pair(String name, T value, boolean bBits) {
		this.bBits = bBits;
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	public boolean isBits() {
		return bBits;
	}

}
