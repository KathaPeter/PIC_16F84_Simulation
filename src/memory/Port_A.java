package memory;

public class Port_A {
	// TRIS == 1: Input; TRIS == 0: Output
	private static volatile int TRISA;
	private static volatile int PORTA;
	public static volatile PIimpl iPortA = new PIimpl();
	// Save last RA4 incoming state for edge detection -> p. 16
	private static volatile Boolean lastRA4 = null; // variable for tri-state: null, 1 or 0

	public static int getPORTA() {
		// Reading the PORTA register reads also the status of the pins -> p.21
		update_writeFromOut();
		return PORTA;
	}

	public static int getTRISA() {
		return TRISA;
	}

	// Method write from System
	public static void writeFromInPORTA(int data8Bit) {
		// Modify only first 5bits -> p.14
		PORTA = data8Bit & 0x1F;
		update_writeOut();
	}

	public static void writeFromInTRISA(int data8Bit) {
		// Modify only first 5bits -> p.14
		TRISA = data8Bit & 0x1F;
		update_writeOut();
		update_writeFromOut();
	}

	public static void update_writeOut() {
		//read-modify-write operation -> p.21
		for (int i = 0; i < 5; ++i) {
			int bitmask = 1 << i;
			// 0 == Output -> Pin is in Output-Mode
			if ((TRISA & bitmask) == 0) {
				//boolean for checkBox in gui and i = indexPin of PORTA
				iPortA.writeOut(((PORTA & bitmask) != 0), i);
			}
		}
	}

	public static void update_writeFromOut() { 
		for (int i = 0; i < 5; ++i) {
			// 1 == Input -> Pin is in Input-Mode
			if (Util.isBit(TRISA, i)) {
				//Clear LED and get state from Switch/InputPin
				boolean bPin = iPortA.writeIn(i);	

				// Write bits to PORTA 
				if (bPin) {
					// if true then set ones
					PORTA = Util.setBit(PORTA, i);
				} else {
					//else clear bit
					PORTA = Util.clearBit(PORTA, i);
				}

				// RA4 is bit 4 in PORTA
				if (i == 4) {
					
					if (boxC(bPin)) {
						//Tick from RA4
						Prescaler.muxA(true);
					}
				}

			}
		}
	}

	private static boolean boxC(boolean bPin) {
		boolean bEvent = false;
		
		if (lastRA4 != null) {
			//T0SE 4th bit in OPTION
			if (Data_Memory.isT0SE()) {
				//Transition high to low -> p.16 (T0SE)
				if(lastRA4 && !bPin) {
					bEvent = true;
				}
			} else {
				//Transition low to high 
				if(!lastRA4 && bPin) {
					bEvent = true;
				}
			}
		}
		
		//Store last Pinstate in RA4
		lastRA4 = bPin;
		
		return bEvent;
	}

	public static int getInternalPORTA() {
		return PORTA;
	}

	public static Boolean[] getLEDs() {
		return iPortA.getLEDs();
	}
}
