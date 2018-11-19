package processing;

import data.Name;

import java.util.HashMap;
import java.util.List;

public class EfficiencyCalculator {

	public static double calculateTruePositives(List<Name> cleanedNames, List<Name> correctNames) {
		double numberOfCorrectNames = 0;
		HashMap<String, Name> hashMap = new HashMap<>();

		for (Name correctName : correctNames) {
			hashMap.put(correctName.toString(), correctName);
		}

		// calculate number of names which appear in both lists
		for (Name cleanedName : cleanedNames) {
			if (hashMap.containsKey(cleanedName.toString())) {
				numberOfCorrectNames++;
			}
		}

		// return the true positive rate
		return numberOfCorrectNames / ((double) correctNames.size());
	}

}
