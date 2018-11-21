package processing;

import data.Name;
import data.NameDistancePair;
import data.OriginalProcessedPair;
import similarity_measures.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This correcter is used to combine multiple similarity measures to achieve a better True Positive Rate.
 */
public class OptimalCorrecter extends Correcter {

	private List<String> femaleFirstNames, maleFirstNames, lastNames;
	private ISimilarityMeasure similarityMeasure, decisionMeasure;

	public OptimalCorrecter(List<String> femaleFirstNames, List<String> maleFirstNames, List<String> lastNames) {
		this.femaleFirstNames = femaleFirstNames;
		this.maleFirstNames = maleFirstNames;
		this.lastNames = lastNames;

		// we distinguish two similarity measures:
		// - one to retrieve a set of names with the same (best) score in the first measure (similarityMeasure)
		// - the second to pick from this list the name with the best score in the second measure (decisionMeasure)
		similarityMeasure = new LevenshteinDistance(); // first measure
		decisionMeasure = new JaccardDistance(); // second measure

		/*
		TPR's of different combinations of similarity and decision measures. (similarity | decision)

		hamming | soundex:      TPR = 0.287
		hamming | levenshtein:  TPR = 0.420
		hamming | jaccard:      TPR = 0.422
		soundex | hamming:      TPR = 0.325
		soundex | levenshtein:  TPR = 0.499
		soundex | jaccard:      TPR = 0.500
		levenshtein | hamming:  TPR = 0.594
		levenshtein | soundex:  TPR = 0.617
		levenshtein | jaccard:  TPR = 0.767 <- best
		jaccard | hamming:      TPR = 0.586
		jaccard | soundex:      TPR = 0.627
		jaccard | levenshtein:  TPR = 0.728
		 */
	}

	// _________________________________________________________________________________________________________________
	/**
	 * This method calculates the estimated correct names for a given list of wrong names.
	 * The similartiy measure in the argument is not used, as we define our own in the constructor.
	 * @param wrongNames to correct
	 * @param notUsed
	 * @return list with corrected names
	 */
	public List<Name> correctNames(List<Name> wrongNames, ISimilarityMeasure notUsed) {
		List<Name> correctedNames = new ArrayList<>(1000);
		int length = wrongNames.size();
		int partLength = length / 100;
		int progress = 0;
		int i = 0;
		ISimilarityMeasure decisionMeasure = new SoundexDistance();

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
			List<String> bestFemaleFirstNames = closestStrings(firstNameProcessed, femaleFirstNamesProcessed, similarityMeasure);
			List<String> bestMaleFirstNames = closestStrings(firstNameProcessed, maleFirstNamesProcessed, similarityMeasure);

			// combining the two first name lists
			bestFemaleFirstNames.addAll(bestMaleFirstNames);
			List<String> bestFirstNames = bestFemaleFirstNames;

			// now we find out which of the first names fits best in regard to our decision measure and put it into our newName
			newName.setFirstName(findBestDecisionFit(name.getFirstName(), bestFirstNames));

			// we also find the best fit for the lastname and put it into the newName
			List<String> bestLastNames = closestStrings(lastNameProcessed, lastNamesProcessed, similarityMeasure);

			// we find the best fitting last name in regard to our decision measure
			newName.setLastName(findBestDecisionFit(name.getLastName(), bestLastNames));

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

		return correctedNames;
	}

	// _________________________________________________________________________________________________________________
	/**
	 * Goes through a list of names and returns the one with the least distance in regard to the decision measure.
	 * @param searchTerm to calculate the distance from
	 * @param names to search
	 * @return best fit in regard to decision measure
	 */
	private String findBestDecisionFit(String searchTerm, List<String> names) {
		int leastDistance = -1;
		String bestName = "";
		searchTerm = decisionMeasure.preProcess(searchTerm);

		for (String name : names) {
			int distance = decisionMeasure.distance(searchTerm, decisionMeasure.preProcess(name));
			if (leastDistance == -1 || distance < leastDistance) {
				leastDistance = distance;
				bestName = name;
			}
		}
		return bestName;
	}

	// _________________________________________________________________________________________________________________
	/**
	 * Collects the list of names with the same and least distance from the wrongString from the correctList in regard to the similarity measure.
	 * @param wrongString to calculate distance from
	 * @param correctList to search in
	 * @param similarityMeasure to apply
	 * @return list of names with least distance
	 */
	private List<String> closestStrings(String wrongString, List<OriginalProcessedPair> correctList, ISimilarityMeasure similarityMeasure) {
		int leastDistance = -1;
		List<String> bestFits = new ArrayList<>(8);

		for (OriginalProcessedPair correct: correctList) {
			int distance = similarityMeasure.distance(wrongString, correct.getProcessed());

			// there is no least distance yet -> regard current distance as least distance
			if (leastDistance == -1) { leastDistance = distance; }

			if (distance < leastDistance) { // a better distance is found -> disregard list of names that were previously best
				leastDistance = distance;
				bestFits.clear();
				bestFits.add(correct.getOriginal());
			} else if (distance == leastDistance) { // a name with the same distance is found -> add it to the list
				bestFits.add(correct.getOriginal());
			}
		}

		return bestFits;
	}

}
