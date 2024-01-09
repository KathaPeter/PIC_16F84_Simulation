package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.ALU8Bit;
import memory.Data_Memory;
import memory.Special_Memory;

public class IORWF extends AExecutor {

	@Override
	public void execute(Decoder_Instruction instObject) {
		
		int valFile8Bit = Data_Memory.read8bit(instObject.address7Bit);

		int valResult8Bit = ALU8Bit.or(Special_Memory.getWReg(), valFile8Bit);
	
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
