package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class CLRW extends AExecutor{
	
	@Override
	public void execute(Decoder_Instruction instObject) {
		Special_Memory.setWReg(0);

		ALU8Bit.flagZ = true;
		updateZFlag();
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name();
	}

}
