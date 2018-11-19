package similarity_measures;

public class LevenshteinDistance implements ISimilarityMeasure{

	@Override
	public int distance(String one, String two) {
		// Setup a matrix of size one.length() X two.length()
		// Put the characters of each string into the first row resp. column
		int[][] matrix = new int[one.length()+1][two.length()+1];
		for (int i=0; i<one.length()+1; i++) {
			matrix[i][0] = i;
		}
		for (int j=0; j<two.length()+1; j++) {
			matrix[0][j] = j;
		}

		for (int j=1; j<two.length()+1; j++) {
			for (int i=1; i<one.length()+1; i++) {
				int subCost = 0;
				// Increase cost of substitution (temp variable) if characters one(i) and two(j) are not the same.
				if (one.charAt(i-1) != two.charAt(j-1)) {
					subCost++;
				}
				// In order, the three measures represent deletion, insertion, and substitution.
				// Take the smallest, of course.
				matrix[i][j] = Math.min(matrix[i-1][j]+1, Math.min(matrix[i][j-1]+1, matrix[i-1][j-1]+subCost));
			}
		}
		return matrix[one.length()][two.length()];
	}

	@Override
	public String preProcess(String name) {
		return name;
	}
}
