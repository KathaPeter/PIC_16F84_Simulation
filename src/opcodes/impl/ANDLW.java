package opcodes.impl;

import memory.ALU8Bit;
import memory.Special_Memory;
import opcodes.AExecutor;
import pipeline.Decoder_Instruction;

public class ANDLW extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {

		int valResult8Bit = ALU8Bit.and(Special_Memory.getWReg(), instObject.constant);
		Special_Memory.setWReg(valResult8Bit);
		
		updateZFlag();
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "  " + toHex(instObject.constant, 2) + "h";
	}

}
