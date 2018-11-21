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
	private List<ISimilarityMeasure> similarityMeasures = new ArrayList<>(2);

	public OptimalCorrecter(List<String> femaleFirstNames, List<String> maleFirstNames, List<String> lastNames) {
		this.femaleFirstNames = femaleFirstNames;
		this.maleFirstNames = maleFirstNames;
		this.lastNames = lastNames;

		similarityMeasures.add(new LevenshteinDistance());
		similarityMeasures.add(new JaccardDistance());
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

			List<String> bestFirstNames = new ArrayList<>(similarityMeasures.size());
			List<String> bestLastNames = new ArrayList<>(similarityMeasures.size());

			for (ISimilarityMeasure similarityMeasure : similarityMeasures) {
				// preprocessing the string (this is useful for the soundex algorithm)
				String firstNameProcessed = similarityMeasure.preProcess(name.getFirstName());
				String lastNameProcessed = similarityMeasure.preProcess(name.getLastName());

				// we calculate the best fit for the first name for both genders
				NameDistancePair femaleFirstName = closestString(firstNameProcessed, femaleFirstNamesProcessed, similarityMeasure);
				NameDistancePair maleFirstName = closestString(firstNameProcessed, maleFirstNamesProcessed, similarityMeasure);

				// now we find out which of the names (female, male) fits better and put it into our newName
				if (femaleFirstName.getDistance() < maleFirstName.getDistance()) {
					bestFirstNames.add(femaleFirstName.getName());
				} else {
					bestFirstNames.add(maleFirstName.getName());
				}

				// we also find the best fit for the lastname and put it into the newName
				bestLastNames.add(closestString(lastNameProcessed, lastNamesProcessed, similarityMeasure).getName());
			}

			// from the list of best first and last names, we chose the one that best fits with our decision measure
			String testName = decisionMeasure.preProcess(name.getFirstName());
			String bestName = testName;
			int leastDistance = -1;
			for (String firstName : bestFirstNames) {
				int distance = decisionMeasure.distance(testName, decisionMeasure.preProcess(firstName));
				if (leastDistance == -1 || distance < leastDistance) {
					bestName = firstName;
					leastDistance = distance;
				}
			}
			newName.setFirstName(bestName);

			testName = decisionMeasure.preProcess(name.getLastName());
			bestName = testName;
			leastDistance = -1;
			for (String lastName : bestLastNames) {
				int distance = decisionMeasure.distance(testName, decisionMeasure.preProcess(lastName));
				if (leastDistance == -1 || distance < leastDistance) {
					bestName = lastName;
					leastDistance = distance;
				}
			}
			newName.setLastName(bestName);

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

	private List<OriginalProcessedPair> processList(List<String> list) {
		List<OriginalProcessedPair> outputList = new ArrayList<>(20000);
		for (String string : list) {
			outputList.add(new OriginalProcessedPair(string, string));
		}
		return outputList;
	}

}
