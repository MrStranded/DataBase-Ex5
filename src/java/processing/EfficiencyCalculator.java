package processing;

import data.Name;

import java.util.List;

public class EfficiencyCalculator {

	public static double calculateTruePositives(List<Name> cleanedNames, List<Name> correctNames) {
		double numberOfCorrectNames = 0;

		// calculate number of names which appear in both lists
		for (Name cleanedName : cleanedNames) {
			for (Name correctName : correctNames) {
				if (correctName.equals(cleanedName)) {
					numberOfCorrectNames++;
					break;
				}
			}
		}

		// return the true positive rate
		return numberOfCorrectNames / ((double) correctNames.size());
	}

}
