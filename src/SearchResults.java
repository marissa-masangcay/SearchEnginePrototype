import java.util.Comparator;
/**
 * This class consists exclusively of static methods that operate on or return
 * on {@link SearchResult} objects.
 */

public class SearchResults {
	
	/**
     * A {@link Comparator} that compares by frequency. If the frequencies
     * are equal, compares by initial position. If the initial positions are equal,
     * compares by file name.
     *
     * @see SearchResult#compareTo(SearchResult)
     * @see String#compareTo(String)
     * @see Integer#compare(int, int)
     */
    public static final Comparator<SearchResult> ORDER_BY_SEARCH_RESULT = new SearchResults.searchResultComparator(); 
    
    public static class searchResultComparator implements Comparator<SearchResult> 
    {

		@Override
		public int compare(SearchResult result1, SearchResult result2) {
			int frequency = Integer.compare(result2.getFrequency(), result1.getFrequency());
    		if (frequency == 0)
    		{
    			//Compare initial position
    			int initialPosition = Integer.compare(result1.getInitialPosition(), result2.getInitialPosition());
    			if (initialPosition == 0)
    			{
    				//Compare ISBN
    				Comparator<String> fileNameComparator = String.CASE_INSENSITIVE_ORDER;
    				int fileName = fileNameComparator.compare(result1.getFileName(), result2.getFileName());
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
		}

    }

}
