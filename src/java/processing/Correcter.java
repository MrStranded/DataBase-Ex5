package processing;

import data.Name;
import data.NameDistancePair;
import data.OriginalProcessedPair;
import similarity_measures.ISimilarityMeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract parent class for the two correctors NameCorrecter and OptimalCorrecter, which provides some basic functionality.
 */
public abstract class Correcter {

	// _________________________________________________________________________________________________________________

	/**
	 * Abstract method which provides framework for correcting a list of names.
	 * @param wrongNames to correct
	 * @param similarityMeasure to apply
	 * @return corrected name list
	 */
	public abstract List<Name> correctNames(List<Name> wrongNames, ISimilarityMeasure similarityMeasure);

	// _________________________________________________________________________________________________________________
	/**
	 * Finds the closest string from the given list for the given string, based on the given similarity measure.
	 * @param wrongString to correct
	 * @param correctList to chose an alternative from
	 * @param similarityMeasure to apply
	 * @return best fitting string from list
	 */
	protected NameDistancePair closestString(String wrongString, List<OriginalProcessedPair> correctList, ISimilarityMeasure similarityMeasure) {
		int leastDistance = -1;
		String bestFit = wrongString;

		for (OriginalProcessedPair correct: correctList) {
			int distance = similarityMeasure.distance(wrongString, correct.getProcessed());

			if (leastDistance == -1 || distance < leastDistance) {
				leastDistance = distance;
				bestFit = correct.getOriginal();
			}
		}

		return new NameDistancePair(bestFit, leastDistance);
	}

	// _________________________________________________________________________________________________________________
	/**
	 * Removes duplicates and returns result.
	 * @param inputList to search for duplicates
	 * @return list with only unique names
	 */
	protected List<Name> removeDuplicates(List<Name> inputList) {
		System.out.println("Removing duplicates...");

		List<Name> outputList = new ArrayList<>(1000);
		HashMap<String, Name> hashMap = new HashMap<>();
		int duplicates = 0;

		for (Name name : inputList) {
			if (!hashMap.containsKey(name.toString())) {
				outputList.add(name);
			} else {
				duplicates++;
			}

			hashMap.put(name.toString(), name);
		}

		System.out.println("Removed " + duplicates + " duplicates. " + outputList.size() + " remain.");

		return inputList;
	}

	// _________________________________________________________________________________________________________________
	/**
	 * Calculates the processed version of each element in the list and constructs a new list,
	 * which contains the original version, as well as the processed one.
	 * @param list to process
	 * @param similarityMeasure to apply
	 * @return list with original string and processed string
	 */
	protected List<OriginalProcessedPair> processList(List<String> list, ISimilarityMeasure similarityMeasure) {
		List<OriginalProcessedPair> outputList = new ArrayList<>(20000);
		for (String string : list) {
			outputList.add(new OriginalProcessedPair(string, similarityMeasure.preProcess(string)));
		}
		return outputList;
	}

	// _________________________________________________________________________________________________________________
	/**
	 * A small method to visualize the progress of the current similarity measure in percent.
	 * @param progress
	 */
	protected void updateProgressBar(int progress) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append('\r');

		stringBuilder.append(progress).append(" % [");
		for (int i=0; i<100; i++) {
			if (i < progress) {
				stringBuilder.append('#');
			} else {
				stringBuilder.append(' ');
			}
		}
		stringBuilder.append(']');

		System.out.print(stringBuilder);
	}

}
