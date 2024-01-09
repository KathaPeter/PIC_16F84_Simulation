package opcodes.impl;

import memory.ALU8Bit;
import memory.Special_Memory;
import opcodes.AExecutor;
import pipeline.Decoder_Instruction;

public class ADDLW extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {

		int val = ALU8Bit.add(Special_Memory.getWReg(), instObject.constant);
		Special_Memory.setWReg(val);
		
		updateFlags();
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "  " + toHex(instObject.constant, 2) + "h";
	}

}
