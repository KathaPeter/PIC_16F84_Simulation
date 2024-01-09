package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class SWAPF extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {

		int val = Data_Memory.read8bit(instObject.address7Bit);

		int tmp1 = (val >> 4) & 0x0F;
		int tmp2 = (val << 4) & 0xF0;
		val = tmp1 + tmp2;

		if (instObject.eDest == 0) {
			Special_Memory.setWReg(val);
		} else {
			Data_Memory.write(instObject.address7Bit, val, false);
		}
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "  " + toHex(instObject.address7Bit, 2) + "h, " + instObject.eDest;
	}

}
