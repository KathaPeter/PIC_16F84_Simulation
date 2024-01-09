package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.Data_Memory;
import memory.Special_Memory;

public class MOVWF extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		
		Data_Memory.write(instObject.address7Bit, Special_Memory.getWReg(), false);
	
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		
		return instObject.eOpCode.name() + "  " + toHex(instObject.address7Bit, 2) + "h";
	
	}

}
