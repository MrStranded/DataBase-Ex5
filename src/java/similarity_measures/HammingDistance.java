package similarity_measures;

import javax.lang.model.element.Name;

public class HammingDistance implements ISimilarityMeasure{

	@Override
	public int distance(String one, String two) {
		int minLength = Math.min(one.length(), two.length());
		int distance = 0;

		for (int i=0; i<minLength; i++) {
			if (one.charAt(i) != two.charAt(i)) {
				distance++;
			}
		}

		return distance;
	}

	@Override
	public String preProcess(String name) {
		return name;
	}
}
