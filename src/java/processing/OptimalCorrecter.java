package processing;

import data.Name;
import data.NameDistancePair;
import data.OriginalProcessedPair;
import similarity_measures.ISimilarityMeasure;
import similarity_measures.JaccardDistance;
import similarity_measures.LevenshteinDistance;
import similarity_measures.SoundexDistance;

import java.util.ArrayList;
import java.util.List;

public class OptimalCorrecter extends Correcter {

	private List<String> femaleFirstNames, maleFirstNames, lastNames;
	private ISimilarityMeasure similarityMeasure, decisionMeasure;

	public OptimalCorrecter(List<String> femaleFirstNames, List<String> maleFirstNames, List<String> lastNames) {
		this.femaleFirstNames = femaleFirstNames;
		this.maleFirstNames = maleFirstNames;
		this.lastNames = lastNames;

		similarityMeasure = new LevenshteinDistance();
		decisionMeasure = new JaccardDistance();
	}

	public List<Name> correctNames(List<Name> wrongNames, ISimilarityMeasure notUsed) {
		List<Name> correctedNames = new ArrayList<>(1000);
		int length = wrongNames.size();
		int partLength = length / 100;
		int progress = 0;
		int i = 0;
		ISimilarityMeasure decisionMeasure = new SoundexDistance();

		System.out.println("Preprocessing lists...");
		List<OriginalProcessedPair> femaleFirstNamesProcessed = processList(femaleFirstNames);
		List<OriginalProcessedPair> maleFirstNamesProcessed = processList(maleFirstNames);
		List<OriginalProcessedPair> lastNamesProcessed = processList(lastNames);

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

		return wrongNames;
	}

	private String findBestDecisionFit(String searchTerm, List<String> names) {
		int leastDistance = -1;
		String bestName = "";
		for (String name : names) {
			int distance = decisionMeasure.distance(searchTerm, name);
			if (leastDistance == -1 || distance < leastDistance) {
				leastDistance = distance;
				bestName = name;
			}
		}
		return bestName;
	}

	private List<String> closestStrings(String wrongString, List<OriginalProcessedPair> correctList, ISimilarityMeasure similarityMeasure) {
		int leastDistance = -1;
		List<String> bestFits = new ArrayList<>(8);

		for (OriginalProcessedPair correct: correctList) {
			int distance = similarityMeasure.distance(wrongString, correct.getProcessed());

			if (leastDistance == -1) {
				leastDistance = distance;
			}

			if (distance < leastDistance) {
				leastDistance = distance;
				bestFits.clear();
				bestFits.add(correct.getOriginal());
			} else if (distance == leastDistance) {
				bestFits.add(correct.getOriginal());
			}
		}

		return bestFits;
	}

	private List<OriginalProcessedPair> processList(List<String> list) {
		List<OriginalProcessedPair> outputList = new ArrayList<>(20000);
		for (String string : list) {
			outputList.add(new OriginalProcessedPair(string, string));
		}
		return outputList;
	}

}
