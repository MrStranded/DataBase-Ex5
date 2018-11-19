import data.Name;
import processing.EfficiencyCalculator;
import similarity_measures.HammingDistance;
import similarity_measures.ISimilarityMeasure;
import processing.NameCorrecter;
import similarity_measures.SoundexDistance;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		// interpreting the given arguments
		List<ISimilarityMeasure> similarityMeasures = interpretArguments(args);

		// loading the data
		System.out.println("Loading data...");
		List<Name> wrongNames = NameLoader.loadNames("res/cleaningDataset/corruptedNames.txt");

		List<String> femaleFirstNames = NameLoader.loadStrings("res/cleaningDataset/femaleFirstnames.txt");
		List<String> maleFirstNames = NameLoader.loadStrings("res/cleaningDataset/maleFirstnames.txt");
		List<String> lastNames = NameLoader.loadStrings("res/cleaningDataset/lastnames.txt");

		List<Name> generatedNames = NameLoader.loadNames("res/cleaningDataset/generatedNames.txt");

		// initializing the correction handler and the similarity measures
		System.out.println("Initializing correcter...");
		NameCorrecter nameCorrecter = new NameCorrecter(femaleFirstNames, maleFirstNames, lastNames);

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

			System.out.println("--- Calculating the TPR.");
			System.out.println("True Positive Rate = " + EfficiencyCalculator.calculateTruePositives(correctedNames, generatedNames));
		}
	}

	private static List<ISimilarityMeasure> interpretArguments(String[] args) {
		List<ISimilarityMeasure> similarityMeasures = new ArrayList<>(1);

		if (args == null || args.length == 0) { // no arguments given -> apply all
			similarityMeasures.add(new HammingDistance());
			similarityMeasures.add(new SoundexDistance());

		} else {
			for (String argument : args) {
				if ("help".equals(argument) || "h".equals(argument) || "-h".equals(argument)) {
					System.out.println("Use this program as follows:");
					System.out.println("- type 'help' to see the help screen");
					System.out.println("- type no argument to calculate all similarity measures");
					System.out.println("- type the names of the similarity measures you want to calculate");
					System.out.println("  There are the following similarity measures you can use:");
					System.out.println("  -> 'hamming'");
					System.out.println("  -> 'soundex'");
					System.out.println("  -> 'levensthein'");
					System.out.println("  -> 'jaccard'");
					System.out.println("  -> 'optimal'");

				} else if ("hamming".equals(argument)) {
					similarityMeasures.add(new HammingDistance());

				} else if ("soundex".equals(argument)) {
					similarityMeasures.add(new SoundexDistance());

				} else if ("levensthein".equals(argument)) {


				} else if ("jaccard".equals(argument)) {


				} else if ("optimal".equals(argument)) {


				} else {
					System.out.println("The argument '" + argument + "' is invalid!");
				}
			}
		}

		return similarityMeasures;
	}

}
