package memory;

public class Util {
	public static boolean isBit(int register, int ibit) {
		return (register & (1 << ibit)) != 0;
	}

	public static int setBit(int register, int iBit) {
		return register | (1 << iBit);
	}
	
	public static int clearBit(int register, int iBit) {
		int bitmask = (1 << iBit);
		return register & ~bitmask;
	}
}
