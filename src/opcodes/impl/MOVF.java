package opcodes.impl;

import opcodes.AExecutor;
import pipeline.Decoder_Instruction;
import memory.*;

public class MOVF extends AExecutor {

	
	@Override
	public void execute(Decoder_Instruction instObject) {
		
		int valFile8Bit = Data_Memory.read8bit(instObject.address7Bit);

		ALU8Bit.flagZ = valFile8Bit == 0;
		updateZFlag();

		if (instObject.eDest == 0) {
			Special_Memory.setWReg(valFile8Bit);

		} else {
			Data_Memory.write(instObject.address7Bit, valFile8Bit, true);
		}
	}

	@Override
	public String toString(Decoder_Instruction instObject) {
		return instObject.eOpCode.name() + "   " + toHex(instObject.address7Bit, 2) + "h, " + instObject.eDest;
	}

}
