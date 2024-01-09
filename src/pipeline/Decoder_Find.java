package pipeline;

import java.util.Arrays;
import java.util.Comparator;

import algo.search.BinarySearch;

/* This is the class which represents the decoder after fetching instructions.
 * 
 * @ author Felix Schirma & Katharina Peter
 */
public class Decoder_Find {
	
	private final static EOpCode[] OPCS;
	
	static {
		
		OPCS = EOpCode.values();
	
		Arrays.sort(OPCS, new Comparator<EOpCode>() {

			@Override
			public int compare(EOpCode o1, EOpCode o2) {
				return o1.getValue() - o2.getValue();
			}
		});
	}

	/*This method calls binarySearch to find right meant instruction. Then allocates/instantiates the 
	 * object Instruction
	 */
	public static Decoder_Instruction searchInstruction(int instBinary) {

		EOpCode result = BinarySearch.search(OPCS, instBinary);
		return new Decoder_Instruction(result, instBinary);
	}
}
