package memory;

public class Port_B {

	// TRIS == 1: Input TRIS == 0: Output
	private static volatile int TRISB;
	private static volatile int PORTB;

	public static volatile PIimpl iPortB = new PIimpl();
	private static volatile Boolean lastRB0 = null;
	private static volatile Integer lastRB4_7 = null;	//bits 4-7 in PORTB	
	

	public static int getPORTB() {
		update_writeFromOut();
		return PORTB;
	}

	public static void update_writeFromOut() {
		int port_B_in = 0;
		for (int i = 0; i < 8; ++i) {
			int bitmask = 1 << i;
			// 1 == Input -> Pin is in Input-Mode
			if ((TRISB & bitmask) != 0) {
				boolean bPin = iPortB.writeIn(i);

				// Write bits to PORTB
				if (bPin) {
					// if true then set ones
					PORTB |= bitmask;
				} else {
					PORTB &= ~bitmask;
				}
				
				if ( i >= 4 && i <= 7 ) {
					//Collect Pins 4-7
					if(bPin) {		 //true means bPin == 1
						port_B_in |= bitmask;
					}
				}

				// RB0 is bit 0 in PORTB
				if (i == 0) {

					// BOX_A Scribble
					boolean bAEvent = false;
					if (lastRB0 != null) {
						if (Data_Memory.isINTEDG()) {
							// Transition low to high -> p.16 (INTEDG)
							// INTEDG geht auf INTF
							if (!lastRB0 && bPin) {
								bAEvent = true;
							}
						} else {
							// Transition high to low
							if (lastRB0 && !bPin) {
								bAEvent = true;
							}
						}
					}

					lastRB0 = bPin;
					if (bAEvent) {
						Data_Memory.setINTF();
					}
				}

			}
		}
		
		
		//BOX_B Scribble
		boolean bBEvent = false;
		if (lastRB4_7 != null) {
			if(lastRB4_7 != port_B_in) {
				bBEvent = true;
			}
		}

		//p. 23 -> SLEEP. Just programmer in the system, in the interrupt service routine, can clear the interrupt in PORTB 
		//The user outside the system can set the interrupt
		lastRB4_7 = port_B_in;
		Data_Memory.setRBIF(bBEvent);

		
	}

	public static int getTRISB() {
		return TRISB;
	}

	public static void writeFromInPORTB(int data8Bit) {
		PORTB = data8Bit;
		update_writeOut();
	}

	public static void update_writeOut() {
		for (int i = 0; i < 8; ++i) {
			int bitmask = 1 << i;
			// 0 == Output -> Pin is in Output-Mode
			if ((TRISB & bitmask) == 0) {
				iPortB.writeOut(((PORTB & bitmask) != 0), i);
			}
		}
	}

	public static void writeFromInTRISB(int data8Bit) {
		TRISB = data8Bit;
		update_writeFromOut();
		update_writeOut();
	}

	public static int getInternalPORTB() {
		return PORTB;
	}

	public static Boolean[] getLEDs() {
		return iPortB.getLEDs();
	}

}
