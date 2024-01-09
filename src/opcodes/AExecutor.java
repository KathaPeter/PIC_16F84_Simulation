package opcodes;

import memory.ALU8Bit;
import memory.Data_Memory;

public abstract class AExecutor implements IExecutor {

	protected String resize(String hexString, int i) {
		while (hexString.length() < i) {
			hexString = '0' + hexString;
		}
		return hexString;
	}

	protected String toHex(int value, int digits) {
		return resize(Integer.toHexString(value), digits);
	}

	protected void updateZFlag() {
		Data_Memory.updateZFlag(ALU8Bit.flagZ);
	}

	protected void updateCFlag() {
		Data_Memory.updateCFlag(ALU8Bit.flagC);
	}

	protected boolean isCFlag() {
		int valStatus8Bit = Data_Memory.read8bit(Data_Memory.ADDRESS_STATUS);
		return ((valStatus8Bit & 0x1) != 0);
	}

	protected void updateFlags() {
		Data_Memory.updateAllFlags(ALU8Bit.flagC, ALU8Bit.flagDC, ALU8Bit.flagZ);
	}

}
