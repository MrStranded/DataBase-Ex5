package processing;

import com.sun.org.apache.xpath.internal.operations.Or;
import data.Name;
import data.NameDistancePair;
import data.OriginalProcessedPair;
import similarity_measures.ISimilarityMeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class NameCorrecter extends Correcter {

	private List<String> femaleFirstNames, maleFirstNames, lastNames;
	private List<OriginalProcessedPair> femaleFirstNamesProcessed, maleFirstNamesProcessed, lastNamesProcessed;

	public NameCorrecter(List<String> femaleFirstNames, List<String> maleFirstNames, List<String> lastNames) {
		this.femaleFirstNames = femaleFirstNames;
		this.maleFirstNames = maleFirstNames;
		this.lastNames = lastNames;
	}

	// _________________________________________________________________________________________________________________

	@Override
	public List<OriginalProcessedPair> getFemaleFirstNamesProcessed() {
		return femaleFirstNamesProcessed;
	}

	@Override
	public List<OriginalProcessedPair> getMaleFirstNamesProcessed() {
		return maleFirstNamesProcessed;
	}

	@Override
	public List<OriginalProcessedPair> getLastNamesProcessed() {
		return lastNamesProcessed;
	}

	/**
	 * For each name in the list, we find the best fitting name based on the given similarity measure.
	 * This corrected name list is also checked for duplicates.
	 * @param wrongNames list of names to correct
	 * @param similarityMeasure to apply
	 * @return list of corrected names without duplicates
	 */
	public Queue<Name> correctNames(List<Name> wrongNames, ISimilarityMeasure similarityMeasure) {
		Queue<Name> correctedNames = new ConcurrentLinkedQueue<>();
		int length = wrongNames.size();
		part = length / 100;

		System.out.println("Preprocessing lists...");
		femaleFirstNamesProcessed = processList(femaleFirstNames, similarityMeasure);
		maleFirstNamesProcessed = processList(maleFirstNames, similarityMeasure);
		lastNamesProcessed = processList(lastNames, similarityMeasure);

		// splitting the list is necessary for multithreaded processing of names
		System.out.println("Splitting name list...");
		ListPointer[] listArray = splitList(wrongNames);

		// we create multiple threads and fill them with the necessary data and run them
		System.out.println("Correcting names...");
		CorrecterThread[] threads = new CorrecterThread[listArray.length];
		int t = 0;

		for (ListPointer listPointer : listArray) {
			threads[t] = new CorrecterThread();
			threads[t].setData(listPointer.getList(), correctedNames, similarityMeasure, this);
			threads[t].start();
			t++;
		}

		// initial 0% display
		updateProgressBar(0);

		// wait for all threads to finish
		for (CorrecterThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// displaying 100% and going to next line after progress bar
		System.out.println("");

		// before returning the correctedNames list, we remove duplicates
		return removeDuplicates(correctedNames);
	}

}
