package processing;

import data.Name;
import data.NameDistancePair;
import data.OriginalProcessedPair;
import similarity_measures.ISimilarityMeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

/**
 * Abstract parent class for the two correctors NameCorrecter and OptimalCorrecter, which provides some basic functionality.
 */
public abstract class Correcter {

	// how many CorrecterThreads should be run
	protected final int CORES = 4;

	// states needed fo progress bar
	private int progress = 0;
	protected int part = 0;

	// _________________________________________________________________________________________________________________
	// for the threads to access the processed data
	public abstract List<OriginalProcessedPair> getFemaleFirstNamesProcessed();
	public abstract List<OriginalProcessedPair> getMaleFirstNamesProcessed();
	public abstract List<OriginalProcessedPair> getLastNamesProcessed();

	// _________________________________________________________________________________________________________________
	/**
	 * Abstract method which provides framework for correcting a list of names.
	 * @param wrongNames to correct
	 * @param similarityMeasure to apply
	 * @return corrected name list
	 */
	public abstract Queue<Name> correctNames(List<Name> wrongNames, ISimilarityMeasure similarityMeasure);

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
	 * Internal class for storing lists in arrays. Somehow java does not allow for List<Name>[].
	 */
	protected class ListPointer {
		private List<Name> list;

		public ListPointer(List<Name> list) {
			this.list = list;
		}

		public List<Name> getList() {
			return list;
		}
	}

	// _________________________________________________________________________________________________________________
	/**
	 * Splits the given name list into parts. The number of parts is equal to the value of the CORES variable.
	 * @param list to split
	 * @return array of lists, packed into ListPointers
	 */
	protected ListPointer[] splitList(List<Name> list) {
		int size = list.size();
		int part = size / CORES;
		ListPointer[] listArray = new ListPointer[CORES];
		// creating the list pointers
		for (int c=0; c<CORES; c++) {
			listArray[c] = new ListPointer(new ArrayList<Name>(part));
		}

		int i = 0;
		int currentList = 0;
		// filling the names into the appropriate list pointers
		for (Name name : list) {
			listArray[currentList].getList().add(name);
			if (i > part*(currentList+1)) {
				currentList++;
				if (currentList >= CORES) {
					currentList = CORES - 1;
				}
			}
			i++;
		}

		return listArray;
	}

	// _________________________________________________________________________________________________________________
	/**
	 * I'm stupid. We don't need that. Why don't we delete this method?
	 * It would serve to recombine a list pointer array into a single list.
	 * @param listArray to merge
	 * @return merged list
	 */
	protected List<Name> mergeList(ListPointer[] listArray) {
		List<Name> outputList = new ArrayList<>(listArray[0].getList().size()*CORES);

		for (ListPointer listPointer : listArray) {
			outputList.addAll(listPointer.getList());
		}

		return outputList;
	}

	// _________________________________________________________________________________________________________________
	/**
	 * Removes duplicates and returns result.
	 * @param inputList to search for duplicates
	 * @return list with only unique names
	 */
	protected Queue<Name> removeDuplicates(Queue<Name> inputList) {
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

	// _________________________________________________________________________________________________________________
	/**
	 * A part signifies how many entries in the wrong-names-list correspond to one percent of completion.
	 * @return the size of a part = percent of completion
	 */
	public int getPart() {
		return part;
	}

	// _________________________________________________________________________________________________________________
	/**
	 * Adds one to the progress in percent and displays it.
	 */
	public void addPercent() {
		progress++;
		updateProgressBar(progress);
	}

}
