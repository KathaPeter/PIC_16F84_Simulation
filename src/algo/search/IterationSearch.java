package algo.search;

import pipeline.EOpCode;

public class IterationSearch {

	public static EOpCode search(EOpCode[] list, int instruction) {

		for (EOpCode op : list) {

			if (op.match(instruction)) {
				return op;
			}
		}

		return null;
	}

}
