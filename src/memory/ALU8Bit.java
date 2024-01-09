package memory;


public class ALU8Bit {

	public static boolean flagZ = false;
	public static boolean flagC = false;
	public static boolean flagDC = false;

	public static int xor(int a, int b) {
		int c = a ^ b;
		c &= 0xFF;

		flagZ = (c == 0);

		return c;
	}

	public static int or(int a, int b) {
		int c = a | b;
		c &= 0xFF;

		flagZ = (c == 0);

		return c;
	}

	public static int and(int a, int b) {
		int c = a & b;
		c &= 0xFF;

		flagZ = (c == 0);

		return c;
	}

	public static int not(int a) {
		int c = ~a;
		c &= 0xFF;

		flagZ = (c == 0);

		return c;
	}

	//rlc -> rotate left through carry
	public static int rlc(int a, boolean cIn) {
		int c = a << 1;
		
		flagC = (c & 0x100) != 0;
		
		if (cIn) {
			c |= 0x1;
		} else {
			c &= 0xFE;
		}
		c &= 0xFF;

		return c;
	}

	//rrc -> rotate right through carry
	public static int rrc(int a, boolean cIn) {

		a &= 0xFF;

		int c;
		if (cIn) {
			a = a | 0x100;
		}
		flagC = (a & 0x1) != 0;
		c = a >> 1;
		c &= 0xFF;

		return c;
	}

	private static int adder(int a, int b) {
		
		int c = (a & 0xFF) + (b & 0xFF);
		//nibble carry_flag
		int flagdc = (a & 0xF) + (b & 0xF);

		flagC = (c > 255);
		flagDC = (flagdc > 15);
		
		c &= 0xFF;
		flagZ = (c == 0);

		return c;
	}

	public static int sub(int a, int b) {
		return adder(a, adder(not(b), 1));
	}

	public static int add(int a, int b) {
		return adder(a, b);
	}

	public static int inc(int a) {
		return add(a, 1);
	}

	public static int dec(int a) {
		return sub(a, 1);
	}

}
