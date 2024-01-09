package opcodes;

import pipeline.Decoder_Instruction;

public interface IExecutor {

	void execute(Decoder_Instruction instObject);
	
	String toString(Decoder_Instruction instObject);

}
