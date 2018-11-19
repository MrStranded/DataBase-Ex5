import data.Name;
import similarity_measures.HammingDistance;
import similarity_measures.ISimilarityMeasure;
import similarity_measures.NameCorrecter;
import similarity_measures.SoundexDistance;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		// loading the data
		System.out.println("Loading data...");
		List<Name> wrongNames = NameLoader.loadNames("res/cleaningDataset/corruptedNames.txt");

		List<String> femaleFirstNames = NameLoader.loadStrings("res/cleaningDataset/femaleFirstnames.txt");
		List<String> maleFirstNames = NameLoader.loadStrings("res/cleaningDataset/maleFirstnames.txt");
		List<String> lastNames = NameLoader.loadStrings("res/cleaningDataset/lastnames.txt");

		// initializing the correction handler and the similarity measures
		System.out.println("Initializing correcter...");
		NameCorrecter nameCorrecter = new NameCorrecter(femaleFirstNames, maleFirstNames, lastNames);

		List<ISimilarityMeasure> similarityMeasures = new ArrayList<>();
		similarityMeasures.add(new HammingDistance());
		similarityMeasures.add(new SoundexDistance());

		// correcting the wrong name list
		for (ISimilarityMeasure similarityMeasure : similarityMeasures) {
			System.out.println("--------- Applying " + similarityMeasure.getClass() + " similarity measure...");

			long startTime = System.currentTimeMillis();
			List<Name> correctedNames = nameCorrecter.correctNames(wrongNames, similarityMeasure);

			System.out.println("It took " + (System.currentTimeMillis() - startTime) + " milliseconds.");

			System.out.println("--- Sample names:");
			int i=0;
			for (Name name : correctedNames) {
				System.out.println(name);
				i++;
				if (i>=3) {
					break;
				}
			}
		}
	}

}
