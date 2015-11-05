/**
 * This class creates search result objects.
 */
public class SearchResult implements Comparable<SearchResult> {

	/* Frequency of the search result */
	private int frequency;

	/* Position first found within the file */
	private int initialPosition;

	/* Name of the file where the word is found */
	private String fileName;

	public SearchResult(String fileName, int frequency, int initialPosition) {
		this.fileName = fileName;
		this.frequency = frequency;
		this.initialPosition = initialPosition;
	}

	/**
	 * Returns the frequency of the key word(s).
	 *
	 * @return number of frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency if it is a non-negative value.
	 *
	 * @param frequency
	 * @return true if the frequency was set
	 */
	public boolean setFrequency(int frequency) {
		if ( frequency < 0 ){
			return false;
		}
		else{
			this.frequency = frequency;
			return true;
		}
	}

	/**
	 * Returns the initial position of the word.
	 *
	 * @return number of position within text file
	 */
	public int getInitialPosition() {
		return initialPosition;
	}

	/**
	 * Sets the initial position if it is a non-negative value.
	 *
	 * @param initialPosition
	 * @return true if the initial position was set
	 */
	public boolean setInitialPosition(int initialPosition) {
		if ( initialPosition < 0 )
		{
			return false;
		}
		else{
			this.initialPosition = initialPosition;
			return true;
		}
	}

	/**
	 * Returns the file name
	 *
	 * @return name of the file
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name if it is non-null and non-empty.
	 *
	 * @param fileName
	 * @return true if the title was set
	 */
	public boolean setFileName(String fileName) {
		if ( fileName == null || fileName.trim().isEmpty() )
		{
			return false;
		}
		else{
			this.fileName = fileName;
			return true;
		}
	}

	@Override
	public String toString() {
		return "SearchResult [fileName="+ fileName + ", frequency=" + frequency + ", initialPosition=" + 
				initialPosition +"]";
	}

	@Override
	public int compareTo(SearchResult other) {
		return 0;
	}
}


