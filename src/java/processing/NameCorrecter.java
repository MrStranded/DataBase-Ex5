package processing;

import com.sun.org.apache.xpath.internal.operations.Or;
import data.Name;
import data.NameDistancePair;
import data.OriginalProcessedPair;
import similarity_measures.ISimilarityMeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NameCorrecter {

	private List<String> femaleFirstNames, maleFirstNames, lastNames;

	public NameCorrecter(List<String> femaleFirstNames, List<String> maleFirstNames, List<String> lastNames) {
		this.femaleFirstNames = femaleFirstNames;
		this.maleFirstNames = maleFirstNames;
		this.lastNames = lastNames;
	}

	/**
	 * For each name in the list, we find the best fitting name based on the given similarity measure.
	 * This corrected name list is also checked for duplicates.
	 * @param wrongNames list of names to correct
	 * @param similarityMeasure to apply
	 * @return list of corrected names without duplicates
	 */
	public List<Name> correctNames(List<Name> wrongNames, ISimilarityMeasure similarityMeasure) {
		List<Name> correctedNames = new ArrayList<>(1000);
		int length = wrongNames.size();
		int partLength = length / 100;
		int progress = 0;
		int i = 0;

		System.out.println("Preprocessing lists...");
		List<OriginalProcessedPair> femaleFirstNamesProcessed = processList(femaleFirstNames, similarityMeasure);
		List<OriginalProcessedPair> maleFirstNamesProcessed = processList(maleFirstNames, similarityMeasure);
		List<OriginalProcessedPair> lastNamesProcessed = processList(lastNames, similarityMeasure);

		System.out.println("Correcting names...");

		for (Name name : wrongNames) {
			// we create a new name that will be added to the correctedNames list
			Name newName = new Name(name.getFirstName(), name.getLastName());

			// preprocessing the string (this is useful for the soundex algorithm)
			String firstNameProcessed = similarityMeasure.preProcess(name.getFirstName());
			String lastNameProcessed = similarityMeasure.preProcess(name.getLastName());

			// we calculate the best fit for the first name for both genders
			NameDistancePair femaleFirstName = closestString(firstNameProcessed, femaleFirstNamesProcessed, similarityMeasure);
			NameDistancePair maleFirstName = closestString(firstNameProcessed, maleFirstNamesProcessed, similarityMeasure);

			// now we find out which of the names (female, male) fits better and put it into our newName
			if (femaleFirstName.getDistance() < maleFirstName.getDistance()) {
				newName.setFirstName(femaleFirstName.getName());
			} else {
				newName.setFirstName(maleFirstName.getName());
			}

			// we also find the best fit for the lastname and put it into the newName
			newName.setLastName(closestString(lastNameProcessed, lastNamesProcessed, similarityMeasure).getName());

			// finally, we add the newName to the correctedNames list (as you can obviously see in the line of code below)
			correctedNames.add(newName);

			// show the poor bois how far we've gotten
			if (i++ > progress * partLength) {
				updateProgressBar(progress);
				progress++;
			}
		}

		// going to next line after progress bar
		System.out.println("");

		// before returning the correctedNames list, we remove duplicates
		return removeDuplicates(correctedNames);
	}

	private List<OriginalProcessedPair> processList(List<String> list, ISimilarityMeasure similarityMeasure) {
		List<OriginalProcessedPair> outputList = new ArrayList<>(20000);
		for (String string : list) {
			outputList.add(new OriginalProcessedPair(string, similarityMeasure.preProcess(string)));
		}
		return outputList;
	}

	/**
	 * Finds the closest string from the given list for the given string, based on the given similarity measure.
	 * @param wrongString to correct
	 * @param correctList to chose an alternative from
	 * @param similarityMeasure to apply
	 * @return best fitting string from list
	 */
	private NameDistancePair closestString(String wrongString, List<OriginalProcessedPair> correctList, ISimilarityMeasure similarityMeasure) {
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

	/**
	 * Removes duplicates and returns result.
	 * @param inputList to search for duplicates
	 * @return list with only unique names
	 */
	private List<Name> removeDuplicates(List<Name> inputList) {
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

	/**
	 * A small method to visualize the progress of the current similarity measure in percent.
	 * @param progress
	 */
	private void updateProgressBar(int progress) {
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