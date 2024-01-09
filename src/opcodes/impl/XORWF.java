package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class XORWF extends AExecutor {
	
	@Override
	public void execute(Decoder_Instruction instObject) {

		int valFile8Bit = Data_Memory.read8bit(instObject.address7Bit);

		int valResult8Bit = ALU8Bit.xor(valFile8Bit, Special_Memory.getWReg());

		updateZFlag();

		if (instObject.eDest == 0) {
			Special_Memory.setWReg(valResult8Bit);

		} else {
			Data_Memory.write(instObject.address7Bit, valResult8Bit, true);
		}

	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "  " + toHex(instObject.address7Bit, 2) + "h, " + instObject.eDest;
	}

}
