package similarity_measures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JaccardDistance implements ISimilarityMeasure{

	private static HashMap<String, List<String>> nameList = new HashMap<>();
	@Override
	public int distance(String one, String two) {
		/*
		 * - First Step is to get TriGrams of both names.
		 * - TriGrams are defined as String Lists (because that's easy).
		 * - Every pair of name and TriGrams is stored in the global Map "nameList" (name is the key, TriGrams is the
		 *   value). We do this drastically increase performance (time needed to run).
		 * - If a pair is not stored yet, its TriGrams will be calculated and the pair is then added to the map.
		 */
		List<String> oneList = new ArrayList<>();
		List<String> twoList = new ArrayList<>();
		if (nameList.containsKey(one)) {
			oneList = nameList.get(one);
		} else {
			generateTriGrams(one, oneList);
			nameList.put(one, oneList);
		}
		if (nameList.containsKey(two)) {
			twoList = nameList.get(two);
		} else {
			generateTriGrams(two, twoList);
			nameList.put(two, twoList);
		}

		/*
		 * - Next step is to count how many TriGrams are common between the two Strings.
		 * - We check the TriGrams of the larger list against the smaller list for simplicity reasons (it allows for a
		 *   simple for-loop).
		 */
		int common = 0;
		if(oneList.size() > twoList.size()) {
			for(int i=0; i<oneList.size(); i++){
				if(twoList.contains(oneList.get(i))){
					common++;
				}
			}
		}
		else {
			for(int i=0; i<twoList.size(); i++){
				if(oneList.contains(twoList.get(i))){
					common++;
				}
			}
		}
		/*
		 * - Similarity is calculated by dividing number of common TriGrams by total number of TriGrams.
		 * - Total amount of TriGrams is calculated by adding the number of TriGrams of both names together and then
		 *   subtracting the number of common Trigrams (since they have been added twice).
		 * - We multiply common by 10000 to get an int between 0 and 10000. This represents percentages with two decimal
		 *   places (0.00% - 100.00%). This should provide enough accuracy for the task at hand.
		 * - Lastly, we subtract the result from 10000, because our code is looking for small numbers (since that works
		 *   for all other similarity measures) and this one measure has it the other way round.
		 */
		return 10000 - ( (common*10000) / (oneList.size()+twoList.size()-common));
	}

	@Override
	public String preProcess(String name) {
		return name;
	}
	private void generateTriGrams(String word, List<String> wordList) {
		word = "##" + word + "##";
		String s;
		for (int i=0; i<word.length()-2; i++) {
			s = Character.toString(word.charAt(i))+Character.toString(word.charAt(i+1))+Character.toString(word.charAt(i+2));
			if(!wordList.contains(s)) {
				wordList.add(s);
			}
		}
	}
}
