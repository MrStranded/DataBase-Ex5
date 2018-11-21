package processing;

import com.sun.org.apache.xpath.internal.operations.Or;
import data.Name;
import data.NameDistancePair;
import data.OriginalProcessedPair;
import similarity_measures.ISimilarityMeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NameCorrecter extends Correcter {

	private List<String> femaleFirstNames, maleFirstNames, lastNames;

	public NameCorrecter(List<String> femaleFirstNames, List<String> maleFirstNames, List<String> lastNames) {
		this.femaleFirstNames = femaleFirstNames;
		this.maleFirstNames = maleFirstNames;
		this.lastNames = lastNames;
	}

	// _________________________________________________________________________________________________________________
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

}
