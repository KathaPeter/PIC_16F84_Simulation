package memory;

public class Special_Memory {
	
	private static int wRegister;

	public static int getWReg() {
		return wRegister;
	}

	public static void setWReg(int value) {
		wRegister = value & 0xFF;	
	}
}
