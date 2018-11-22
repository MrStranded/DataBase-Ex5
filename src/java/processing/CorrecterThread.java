package processing;

import data.Name;
import data.NameDistancePair;
import similarity_measures.ISimilarityMeasure;

import java.util.List;
import java.util.Queue;

public class CorrecterThread extends Thread {

	private List<Name> wrongNames;
	private Queue<Name> correctedNames;
	private ISimilarityMeasure similarityMeasure;
	private Correcter correcter;

	public void setData(List<Name> wrongNames, Queue<Name> correctedNames, ISimilarityMeasure similarityMeasure, Correcter correcter) {
		this.wrongNames = wrongNames;
		this.correctedNames = correctedNames;
		this.similarityMeasure = similarityMeasure;
		this.correcter = correcter;
	}

	@Override
	public void run() {
		int c = 0;

		for (Name name : wrongNames) {
			// we create a new name that will be added to the correctedNames list
			Name newName = new Name(name.getFirstName(), name.getLastName());

			// preprocessing the string (this is useful for the soundex algorithm)
			String firstNameProcessed = similarityMeasure.preProcess(name.getFirstName());
			String lastNameProcessed = similarityMeasure.preProcess(name.getLastName());

			// we calculate the best fit for the first name for both genders
			NameDistancePair femaleFirstName = correcter.closestString(firstNameProcessed, correcter.getFemaleFirstNamesProcessed(), similarityMeasure);
			NameDistancePair maleFirstName = correcter.closestString(firstNameProcessed, correcter.getMaleFirstNamesProcessed(), similarityMeasure);

			// now we find out which of the names (female, male) fits better and put it into our newName
			if (femaleFirstName.getDistance() < maleFirstName.getDistance()) {
				newName.setFirstName(femaleFirstName.getName());
			} else {
				newName.setFirstName(maleFirstName.getName());
			}

			// we also find the best fit for the lastname and put it into the newName
			newName.setLastName(correcter.closestString(lastNameProcessed, correcter.getLastNamesProcessed(), similarityMeasure).getName());

			// finally, we add the newName to the correctedNames list (as you can obviously see in the line of code below)
			correctedNames.add(newName);

			// update the progress bar boi
			c++;
			if (c >= correcter.getPart()) {
				c = 0;
				correcter.addPercent();
			}
		}
	}

}
