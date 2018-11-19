package data;

/**
 * Simple POJO to store the original version of a string and its processed variant.
 */
public class OriginalProcessedPair {

	private String original, processed;

	public OriginalProcessedPair(String original, String processed) {
		this.original = original;
		this.processed = processed;
	}

	public String getOriginal() {
		return original;
	}

	public String getProcessed() {
		return processed;
	}
}
