package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;

public class NOP extends AExecutor{

	@Override
	public void execute(Decoder_Instruction instObject) {
		// Do nothing	
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name();
	}

}
