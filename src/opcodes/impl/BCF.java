package opcodes.impl;

import memory.*;
import opcodes.AExecutor;
import pipeline.Decoder_Instruction;

public class BCF extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {

		int val8Bit = Data_Memory.read8bit(instObject.address7Bit);

		int bitMask = (1 << instObject.targetBit);

		int valResult8Bit = val8Bit & (~bitMask);

		Data_Memory.write(instObject.address7Bit, valResult8Bit, false);
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "    " + toHex(instObject.address7Bit, 2)+ "h, " + instObject.targetBit;
	}

}
