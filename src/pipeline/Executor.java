package pipeline;

public class Executor {

	public static boolean bSkip  = false;

	public static void execute(Decoder_Instruction instObject) {

		//Skip pc in boolean
		if (bSkip) {
			bSkip = false;
		} else {
			instObject.execute();
		}

	}

}
