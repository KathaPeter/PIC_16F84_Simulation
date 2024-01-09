package memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import memory.guiutil.LSTLine;
import pipeline.Decoder_Find;
import pipeline.Decoder_Instruction;
import pipeline.Fetcher;

public class Code_Memory {

	public static int[] cMemory = new int[1024]; // cMemory == 14bits Bus
	private static List<String> lstOrig = new ArrayList<String>();

	private static int lineCounter;
	private static String lstFilePathCurrent;

	public static void fileread(String lstFilePath) {

		lstFilePathCurrent = lstFilePath;

		try {
			lstOrig.clear();
			lineCounter = 0;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(lstFilePathCurrent)));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (!Character.isWhitespace(line.charAt(0))) {
					Code_Memory.cMemory[lineCounter] = Integer.parseInt(find_command(line), 16);
					lineCounter++;
					lstOrig.add(line);
				}
			}

			bufferedReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String find_command(String line) {
		char[] command = new char[4];

		for (int i = 0, j = 0; i < command.length + 1; i++, j++) {
			if (Character.isWhitespace(line.charAt(i + 4))) {
				i++;
				command[j] = line.charAt(i + 4);
			} else {
				command[j] = line.charAt(i + 4);
			}
		}
		return String.copyValueOf(command);
	}

	public static List<LSTLine> getLST() {
		List<LSTLine> result = new ArrayList<LSTLine>();
		for (int i = 0; i < lineCounter; i++) {
			String lstline = "";
			
			int current_Instruction = Fetcher.current_Instruction(i);
			Decoder_Instruction searchInstruction = Decoder_Find.searchInstruction(current_Instruction);
			lstline = searchInstruction.toString();
			
			result.add(new LSTLine(lstline, lstOrig.get(i).substring(28)));
		}
		return result;
	}

	public static int getLineCounter() {
		return lineCounter;
	}

}
