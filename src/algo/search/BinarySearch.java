package algo.search;

import pipeline.EOpCode;

public class BinarySearch {

	public static EOpCode search(EOpCode[] list, int instruction) {
		return binarysearch(0, list.length, list, instruction);
	}

	private static EOpCode binarysearch(int l, int r, EOpCode[] list, int instruction) {
		
		if ( l >= r ) {
			return null;
		}
		
		int m = l + (r - l) / 2;
		EOpCode opcM = list[m];
		int opcvM = opcM.getValue();

		if (opcM.match(instruction)) {
			return opcM;
		} else if (instruction > opcvM) {
			return binarysearch(m + 1, r, list, instruction);
		} else if (instruction < opcvM) {
			return binarysearch(l, m, list, instruction);
		}
		
		throw new RuntimeException("WAU WAU");
	}

}
