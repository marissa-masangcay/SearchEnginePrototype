import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses links from HTML. Assumes the HTML is valid, and all attributes are
 * properly quoted and URL encoded.
 *
 * <p>
 * See the following link for details on the HTML Anchor tag:
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a"> https:
 * //developer.mozilla.org/en-US/docs/Web/HTML/Element/a </a>

 * @see LinkTester
 */
public class LinkParser {

    /**
     * The regular expression used to parse the HTML for links.
     */
    public static final String REGEX = "(?im)<a\\s+.*?\\s*href\\s*=\\s*\"(.*?\\..*?)\"";

    /**
     * The group in the regular expression that captures the raw link.
     */
    public static final int GROUP = 1; 

    /**
     * Parses the provided text for HTML links.
     *
     * @param text - valid HTML code, with quoted attributes and URL encoded links
     * @return list of links found in HTML code
     */
    public static ArrayList<URL> listLinks(URL baseURL, String htmlText) {
        // list to store links
        ArrayList<URL> links = new ArrayList<URL>();

        // compile string into regular expression
        Pattern p = Pattern.compile(REGEX);

        // match provided text against regular expression
        Matcher m = p.matcher(htmlText);

        // loop through every match found in text
        while ( m.find() ) 
        {
            // add the appropriate group from regular expression to list
            try {
				links.add(new URL(baseURL, m.group(GROUP)));
			} catch (MalformedURLException e) {
				System.err.println("Invalid URL");
				e.printStackTrace();
			}
        }
        return links;
    }
}