package similarity_measures;

public class SoundexDistance implements ISimilarityMeasure {

	private String[] consonantClasses = {"BFPV", "CGJQSXZ", "DT", "L", "MN", "R"};

	/**
	 * This implementation calculates the soundex representation of the two strings and counts how many letters differ.
	 * This is a very simple (and stupid) implementation, because the distance can only reach from 0 to 4, which is extremely imprecise.
	 * @param one first string to compare
	 * @param two second string to compare
	 * @return the soundex distance between the two strings
	 */
	@Override
	public int distance(String one, String two) {
		String oneProcessed = soundexify(one);
		String twoProcessed = soundexify(two);

		int distance = 0;

		for (int i=0; i<4; i++) {
			if (oneProcessed.charAt(i) != twoProcessed.charAt(i)) {
				distance++;
			}
		}

		return distance;
	}

	/**
	 * This method converts the given string into the corresponding soundex distance.
	 * The complex rules for the soundex algorithm can be found on wikipedia:
	 * <a href="https://en.wikipedia.org/wiki/Soundex">https://en.wikipedia.org/wiki/Soundex</a>
	 * The implementation used here is the "correct" one and not the one implemented in most SQL languages.
	 * @param input to soundexify
	 * @return output string in soundex format
	 */
	private String soundexify(String input) {
		// taking the first letter
		StringBuilder output = new StringBuilder(input.substring(0,1));

		// setting up some variables that we need to fulfill the complicated soundex specifications
		char lastLetter = output.charAt(0);
		int lastNumber = 0;
		int numberOfNumbers = 0;

		// replacing each consequent letter by the appropriate number (vowels are ignored)
		char[] charArray = input.toCharArray();
		for (int i=1; i<charArray.length; i++) {
			boolean found = false;
			char currentChar = charArray[i];

			// searching the consonantclasses for the current char
			for (int j=0; j<consonantClasses.length; j++) {
				int number = j+1;

				for (char c : consonantClasses[j].toCharArray()) {
					if (c == currentChar) {
						// we only add the number if it's different from the last number, or they are not separated by h or w
						if ((number != lastNumber) || !(lastLetter == 'H' || lastLetter == 'W')) {
							// char found in consonantclass -> append
							output.append(number);
							lastNumber = number;
							numberOfNumbers++;
							found = true;
							break;
						}
					}
				}
				if (found) { break; }
			}

			// updating the last letter
			lastLetter = currentChar;
			// exit if there are enough numbers
			if (numberOfNumbers >= 3) { break; }
		}

		// fill up if there are less than 3 numbers added
		while (numberOfNumbers < 3) {
			output.append('0');
			numberOfNumbers++;
		}

		return output.toString();
	}
}
