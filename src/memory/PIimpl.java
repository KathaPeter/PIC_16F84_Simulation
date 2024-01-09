package memory;

public class PIimpl {

	private boolean[] led = new boolean[8];
	private boolean[] schalter = new boolean[8];
	
	public void writeOut(boolean b, int bit) {
		led[bit] = b;
	}

	public boolean writeIn(int bit) {
		writeOut(false, bit); //Clear LED cause PIN is Input
		return schalter[bit];	
	}

	public Boolean[] getLEDs() {
		Boolean[] value = new Boolean[8];
		for (int i = 0; i < led.length; i++) {
			value[i] = led[i];
		}
		return value;
	}

	public void setSwitch(boolean b, int bit) {
		schalter[bit] = b;
	}

	public void resetLED() {
		for (int i = 0; i < led.length; i++) {
			led[i] = false;
		}
	}

}
