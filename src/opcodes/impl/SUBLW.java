package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;


public class SUBLW extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {

		int val = ALU8Bit.sub(instObject.constant, Special_Memory.getWReg());
		Special_Memory.setWReg(val);
		
		updateFlags();
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "  " + toHex(instObject.constant, 2) + "h";
	}

}
