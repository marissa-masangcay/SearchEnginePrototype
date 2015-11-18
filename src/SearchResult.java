import java.util.Comparator;

/**
 * This class creates search result objects.
 */
public class SearchResult implements Comparable<SearchResult> {

	/* Frequency of the search result */
	private int frequency;

	/* Position first found within the file */
	private int initialPosition;

	/* Name of the file where the word is found */
	private final String fileName;

	/** TODO */
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
		if ( frequency < 0 ) {
			return false;
		}
		else {
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
		else {
			this.initialPosition = initialPosition;
			return true;
		}
	}

	/**
	 * Adds (and updates) input frequency value to current frequency 
	 *
	 * @param frequency
	 * @return true if frequency is greater than or equal to zero
	 */
	public boolean addFrequency(int frequency)
	{
		// TODO Fix formatting
		if ( frequency>=0)
		{
			this.frequency = this.frequency + frequency;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Updates current initial position if input position is less than
	 * current initial position
	 *
	 * @param initialPosition
	 * @return true if the initial position was updated
	 */
	public boolean updatePosition(int position)
	{
		// TODO Formatting
		if ( position< this.initialPosition && position>0)
		{
			this.initialPosition = position;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Updates the search result's frequency and initial position if needed
	 *
	 * @param frequency
	 * @param initialPosition
	 * @return true if the frequency and initial position were updated
	 */
	public boolean update(int frequency, int position)
	{
		return (addFrequency(frequency) && updatePosition(position));
	}
	
	/**
	 * Returns the file name
	 *
	 * @return name of the file
	 */
	public String getFileName() {
		return fileName;
	}


	@Override
	public String toString() {
		return "SearchResult [fileName="+ fileName + ", frequency=" + frequency + ", initialPosition=" + 
				initialPosition +"]";
	}

	/**
	 * Compares search results and sorts them by frequency, initialPosition, 
	 * and text file name
	 *
	 * @param other
	 *    Search result to be compared with
	 * @return the value of the two search results after being compared
	 */
	@Override
	public int compareTo(SearchResult other) {
		
		int frequency = Integer.compare(other.getFrequency(), this.getFrequency());
		if ( frequency == 0 )
		{
			//Compare initial position
			int initialPosition = Integer.compare(this.getInitialPosition(), other.getInitialPosition());
			if ( initialPosition == 0 )
			{
				//Compare fileName
				Comparator<String> fileNameComparator = String.CASE_INSENSITIVE_ORDER;
				int fileName = fileNameComparator.compare(this.getFileName(), other.getFileName());
				return fileName;
			}
			else
			{
				return initialPosition;
			}
		}
		else
		{
			return frequency;
		}
		
		// TODO Simplify
		/*
		if (this.frequency != other.frequency) {
			return Integer.compare(other.frequency, this.frequency);
		}
		
		if (this.initialPosition != other.initialPosition) {
			return Integer.compare(this.initialPosition, other.initialPosition);
		}
		
		return String.CASE_INSENSITIVE_ORDER.compare(this.getFileName(), other.getFileName());
		*/
	}
}


