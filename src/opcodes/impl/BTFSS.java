package opcodes.impl;

import memory.*;
import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import pipeline.Executor;

public class BTFSS extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		int val8Bit = Data_Memory.read8bit(instObject.address7Bit);

		int bitMask = (1 << instObject.targetBit);

		boolean isBitSet = (val8Bit & bitMask) != 0;

		if(isBitSet) {
			Executor.bSkip = true;
		}
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "  " + toHex(instObject.address7Bit, 2) + "h, "+instObject.targetBit;
	}

}
