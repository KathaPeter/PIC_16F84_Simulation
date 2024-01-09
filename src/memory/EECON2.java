package memory;

public class EECON2 {

	private static int state = 0;

	private static int STATE_0 = 0;
	private static int STATE_55 = 1;

	public static boolean EECON2_WR_ENABLE = false;

	// p. 34
	public static void setEECON2(int data8Bit) {
		if (state == STATE_0) {
			if (data8Bit == 0x55) {
				state = STATE_55;
			}
		} else if (state == STATE_55) {
			if (data8Bit == 0xAA) {
				EECON2_WR_ENABLE  = true;
				EEPROM.write();
			}
			state = STATE_0;
		}
	}

}
