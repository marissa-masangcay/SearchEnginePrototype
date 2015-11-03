

public class SearchResult implements Comparable<SearchResult> {

	/* Frequency of the search result */
	private int frequency;
	
	/* Position first found within the file */
	private int initialPosition;
	
	/* Name of the file where the word is found */
	private String fileName;

	public SearchResult(String fileName, int frequency, int initialPosition) {
        this.frequency = frequency;
        this.initialPosition = initialPosition;
        this.fileName = fileName;
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
		if (frequency < 0){
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
		if (initialPosition < 0)
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
		if (fileName == null || fileName.trim().isEmpty()){
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
		// TODO Auto-generated method stub
		return 0;
	}
	
	 /** Inner class for storing comparable frequencies. */
    public static class Frequency implements Comparable<Frequency> {
        /** Initial Position. */
        private final int frequency;

        /** Initialize a frequency. */
        public Frequency(int inputFrequency) {
            this.frequency = inputFrequency;
        }

        @Override
        public String toString() {
            return String.valueOf(frequency);
        }

        @Override
        public int compareTo(Frequency other) {
                return Integer.compare(this.frequency, other.frequency);
        }

        /** Helper method for conveniently creating frequencies. */
        public static Frequency get(int frequency) {
            return new Frequency(frequency);
        }
    }
    
    /** Inner class for storing comparable initial positions. */
    public static class InitialPosition implements Comparable<InitialPosition> {
        /** Initial Position. */
        private final int initialPosition;

        /** Initialize an initial position. */
        public InitialPosition(int inputInitialPosition) {
            this.initialPosition = inputInitialPosition;
        }

        @Override
        public String toString() {
            return String.valueOf(initialPosition);
        }

        @Override
        public int compareTo(InitialPosition other) {
                return Integer.compare(this.initialPosition, other.initialPosition);
        }

        /** Helper method for conveniently creating initial positions. */
        public static InitialPosition get(int initialPosition) {
            return new InitialPosition(initialPosition);
        }
    }
    
    /** Inner class for storing comparable file names. */
    public static class FileName implements Comparable<FileName> {
        /** File Names */
        private final String fileName;

        /** Initialize a file name */
        public FileName(String inputFileName) {
            this.fileName = inputFileName;
        }

        @Override
        public String toString() {
            return this.fileName;
        }
      
		@Override
		public int compareTo(FileName other) {
			return this.fileName.compareTo(other.fileName);
		}

        /** Helper method for conveniently creating file names. */
        public static FileName get(String fileName) {
            return new FileName(fileName);
        }

    }
}


