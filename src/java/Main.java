import data.Name;
import similarity_measures.HammingDistance;
import similarity_measures.ISimilarityMeasure;
import similarity_measures.NameCorrecter;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		// loading the data
		System.out.println("Loading data...");
		List<Name> wrongNames = NameLoader.loadNames("res/cleaningDataset/corruptedNames.txt");

		List<String> femaleFirstNames = NameLoader.loadStrings("res/cleaningDataset/femaleFirstnames.txt");
		List<String> maleFirstNames = NameLoader.loadStrings("res/cleaningDataset/maleFirstnames.txt");
		List<String> lastNames = NameLoader.loadStrings("res/cleaningDataset/lastnames.txt");

		// initializing the correction handler
		System.out.println("Initializing correcter...");
		NameCorrecter nameCorrecter = new NameCorrecter(femaleFirstNames, maleFirstNames, lastNames);

		// correcting the wrong name list
		System.out.println("Applying hamming similarity measure...");
		ISimilarityMeasure hammingDistance = new HammingDistance();

		List<Name> correctedNames = nameCorrecter.correctNames(wrongNames, hammingDistance);

		System.out.println("Corrected Names: -----------------------------");
		for (Name name : correctedNames) {
			System.out.println(name);
		}
	}

}
