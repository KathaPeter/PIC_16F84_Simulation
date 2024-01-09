package pipeline;

public class Decoder_Instruction {

	public EOpCode eOpCode;
	public int eDest;
	public int constant;
	public int address7Bit;
	public int targetBit;
	
	public int origInstruction;
	
	public Decoder_Instruction(EOpCode opc, int instruction) {
		eOpCode = opc;
		this.origInstruction = instruction;
		
		if ( opc.getMask().contains("f") ) {
			this.address7Bit = instruction & 0x007F;
		}
		
		if ( opc.getMask().contains("d") ) {
			if ( (instruction & 0x0080) == 0 ) {
				this.eDest = 0;
			} else {		
				this.eDest = 1;
			}
		}
		
		if ( opc.getMask().contains("b") ) {
			targetBit = (instruction & 0x3FF) >> 7;
		}
		
		if ( opc.getMask().contains("k") ) {
			if (opc.getMask().charAt(3) == 'k') {
				//CALL/GOTO
				this.constant = instruction & 0x7ff;
			} else {
				//OTHER
				this.constant = instruction & 0x00ff;
			}
		}
	}
	
	public void execute() {
		this.eOpCode.execute(this);
	}
	
	//Return origin Command as String
	@Override
	public String toString() {
		return eOpCode.toString(this);
	}
}
