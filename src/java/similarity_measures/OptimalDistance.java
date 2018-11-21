package similarity_measures;

/**
 * This is a place holder class which is only used for the purpose of differentiating whether we need to use the OptimalCorrecter.java or the "normal" NameCorrecter.java.
 * It really does absolutely nothing at all.
 */
public class OptimalDistance implements ISimilarityMeasure {

	@Override
	public int distance(String one, String two) {
		return 0;
	}

	@Override
	public String preProcess(String name) {
		return null;
	}
}
