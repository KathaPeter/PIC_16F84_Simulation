package memory;

public class Ringbuffer {

	public static int[] aRingBuffer;
	private static int pos;
	private static final int iMaxSize = 8;

	static {
		reset();
	}

	public static void push(int retAdress) {
		aRingBuffer[pos] = retAdress;
		pos = (pos + 1) % aRingBuffer.length;
	}

	public static int pop() {
		pos = Math.max(pos - 1, 0);
		int retAdress = aRingBuffer[pos];
		aRingBuffer[pos] = 0;
		return retAdress;
	}

	public static int[] getStackData() {
		int[] value = new int[aRingBuffer.length];

		for (int i = 0; i < value.length; i++) {
			value[i] = aRingBuffer[i];
		}
		return value;
	}

	public static int getStackPos() {
		return pos;
	}

	public static void reset() {
		aRingBuffer = new int[iMaxSize];
		pos = 0;
	}
}
