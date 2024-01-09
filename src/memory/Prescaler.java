
package memory;

public class Prescaler {
	
	private static int PRESCALE_COUNTER = 0;

	//prescaler -> p. 16, p. 30
	public static void muxA(boolean b) {
		if (Data_Memory.isT0CS() == b) {
			//picture p. 30
			muxB(true);
			muxC(false);
		}
	}

	public static void muxB(boolean b) {
		if(Data_Memory.isPSA() == b) {
			Data_Memory.tickTimer0();
		}
	}
	
	
	/**
	 * Checks if Multiplexer_Input (TMRO or WDT) is the same as the bit PSA in OPTION_REGISTER.
	 * If PSA == 0 than prescaler is assigned to TMR0, PSA == 1 than prescaler is assigned to WDT. 
	 * After it calls function tickPrescaler();
	 * @param boolean, indicates where the information comes from (TMR0 or WDT).
	 */
	public static void muxC(boolean b) {
		if(Data_Memory.isPSA() == b) {
			tickPrescaler();
		}
	}
	
	public static void muxD(boolean b) {
		if(Data_Memory.isPSA() == b) {
			tick_WDT_TimeOut();
		}
	}
	
	public static void tickPrescaler() {
		
		//p. 16 (prescaler_rate), 30
		int presc = PRESCALE_COUNTER;
		int bitI = Data_Memory.get_PSbits();
		
		boolean before = Util.isBit(presc, bitI);
		
		presc++;
		
		boolean after = Util.isBit(presc, bitI);
		
		if ( presc > 255 ) {
			presc = 0;
		}
		
		PRESCALE_COUNTER = presc;
		
		//p. 16
		if(before != after) {		
			muxD(true);
			muxB(false);
		}
	}
	
	public static void tick_WDT_TimeOut() {
		if(Data_Memory.isSLEEP()) {
			Data_Memory.set_XReset(EReset_Cause.WDT_WAKEUP);
		}else {
			//WDT_RESET during normal operation
			Data_Memory.set_XReset(EReset_Cause.WDT_RESET);
		}
	}

	public static void resetPrescaler() {
		PRESCALE_COUNTER = 0;
	}
}
