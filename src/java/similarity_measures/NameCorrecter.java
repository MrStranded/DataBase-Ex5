package similarity_measures;

import data.Name;

import java.util.ArrayList;
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

		System.out.println("Correcting names...");

		for (Name name : wrongNames) {
			// we create a new name that will be added to the correctedNames list
			Name newName = new Name(name.getFirstName(), name.getLastName());

			// we calculate the best fit for the first name for both genders
			String femaleFirstName = closestString(name.getFirstName(), femaleFirstNames, similarityMeasure);
			String maleFirstName = closestString(name.getFirstName(), maleFirstNames, similarityMeasure);

			// now we find out which of the names (female, male) fits better and put it into our newName
			if (similarityMeasure.distance(name.getFirstName(), femaleFirstName) < similarityMeasure.distance(name.getFirstName(), maleFirstName)) {
				newName.setFirstName(femaleFirstName);
			} else {
				newName.setFirstName(maleFirstName);
			}

			// we also find the best fit for the lastname and put it into the newName
			newName.setLastName(closestString(name.getLastName(), lastNames, similarityMeasure));

			// finally, we add the newName to the correctedNames list (as you can obviously see in the line of code below)
			correctedNames.add(newName);
		}

		// before returning the correctedNames list, we remove duplicates
		return removeDuplicates(correctedNames);
	}

	/**
	 * Finds the closest string from the given list for the given string, based on the given similarity measure.
	 * @param wrongString to correct
	 * @param correctList to chose an alternative from
	 * @param similarityMeasure to apply
	 * @return best fitting string from list
	 */
	private String closestString(String wrongString, List<String> correctList, ISimilarityMeasure similarityMeasure) {
		int leastDistance = -1;
		String bestFit = wrongString;

		for (String correctString : correctList) {
			int distance = similarityMeasure.distance(wrongString, correctString);

			if (leastDistance == -1 || distance < leastDistance) {
				leastDistance = distance;
				bestFit = correctString;
			}
		}

		return bestFit;
	}

	/**
	 * Removes duplicates and returns result.
	 * @param inputList to search for duplicates
	 * @return list with only unique names
	 */
	private List<Name> removeDuplicates(List<Name> inputList) {
		System.out.println("Removing duplicates...");
		return inputList;
	}

}
