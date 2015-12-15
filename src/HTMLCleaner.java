import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class does not take a particularly efficient approach, but this
 * simplifies the process of retrieving and cleaning HTML code for your
 * web crawler project later.
 */

/**
 * A helper class with several static methods that will help fetch a webpage,
 * strip out all of the HTML, and parse the resulting plain text into words.
 * Meant to be used for the web crawler project.
 *
 * @see HTMLCleaner
 * @see HTMLCleanerTest
 */
public class HTMLCleaner {

    /** Regular expression for removing special characters. */
    public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

    /** Regular expression for splitting text into words by whitespace. */
    public static final String SPLIT_REGEX = "(?U)\\p{Space}+";
    
    public static enum HTTP{
    	OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
    };
    
    /** Version of HTTP used and supported. */
	public static final String version = "HTTP/1.1";
	
	/** Port used by socket. For web servers, should be port 80. */
	public static final int PORT = 80;

//    /**
//     * Fetches the webpage at the provided URL, cleans up the HTML tags, and
//     * parses the resulting plain text into words.
//     *
//     * THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
//     *
//     * @param url
//     *            webpage to download
//     * @return list of parsed words
//     */
//    public static ArrayList<String> fetchWords(String url) {
//        String html = fetchHTML(url);
//        String text = cleanHTML(html);
//        return parseWords(text);
//    }
//
    /**
     * Parses the provided plain text (already cleaned of HTML tags) into
     * individual words.
     *
     *
     * @param text
     *            plain text without html tags
     * @return list of parsed words
     */
    public static ThreadedInvertedIndex parseWords(String text) {
        //ArrayList<String> words = new ArrayList<String>();
    	ThreadedInvertedIndex invertedIndex = new ThreadedInvertedIndex();
        text = text.replaceAll(CLEAN_REGEX, "").toLowerCase();
        
        int position = 1;

        for (String word : text.split(SPLIT_REGEX)) {
            word = word.trim();

            if (!word.isEmpty()) {
                //words.add(word);
            	invertedIndex.add(word, text, position);
            	
            }
            position++; 
        }

        return invertedIndex;
    }

    /**
     * Removes all style and script tags (and any text in between those tags),
     * all HTML tags, and all special characters/entities.
     *
     * THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
     *
     * @param html
     *            html code to parse
     * @return plain text
     */
    public static String cleanHTML(String html) {
        String text = html;
        text = stripElement("script", text);
        text = stripElement("style", text);
        text = stripTags(text);
        text = stripEntities(text);
        return text;
    }

//    /**
//     * Fetches the webpage at the provided URL by opening a socket, sending an
//     * HTTP request, removing the headers, and returning the resulting HTML
//     * code.
//     *
//     * You can use the code provided in class if you prefer.
//     *
//     * Please note this method should not throw any exceptions.
//     *
//     * @param link
//     *            webpage to download
//     * @return html code
//     */
//    public static String fetchHTML(String link) {
//        // TODO Fill in and fix return.
//    	try {
//			URL target = new URL(link);
//			String request = craftHTTPRequest(target, HTTP.GET);
//			List<String> lines = fetchLines(target, request);
//			
//			int start = 0;
//			int end = lines.size();
//			
//			while (!lines.get(start).trim().isEmpty() && start<end)
//			{
//				start++;
//			}
//			
//			Map<String, String> fields = parseHeaders(lines.subList(0, start+1));
//			String type = fields.get("Content-Type");
//			
//			if (type != null && type.toLowerCase().contains("html"))
//			{
//				return String.join(System.lineSeparator(), lines.subList(start+1, end));
//			}
//			
//			return null;
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        return null;
//    }
//    
//    /**
//	 * Will connect to the web server and fetch the URL using the HTTP
//     * request provided. It would be more efficient to operate on each
//     * line as returned instead of storing the entire result as a list.
//     *
//	 * @param url - url to fetch
//	 * @param request - full HTTP request
//	 *
//	 * @return the lines read from the web server
//	 *
//	 * @throws IOException
//	 * @throws UnknownHostException
//	 */
//	public static List<String> fetchLines(URL url, String request)
//	        throws UnknownHostException, IOException
//	{
//	    ArrayList<String> lines = new ArrayList<>();
//
//        try (
//            Socket socket = new Socket(url.getHost(), PORT);
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(socket.getInputStream()));
//            PrintWriter writer = new PrintWriter(socket.getOutputStream());
//        ) {
//            writer.println(request);
//            writer.flush();
//
//            String line = null;
//
//            while ((line = reader.readLine()) != null) {
//                lines.add(line);
//            }
//        }
//
//	    return lines;
//	}
//
//	 
//    /**
//	 * Helper method that parses HTTP headers into a map where the key is
//	 * the field name and the value is the field value. The status code
//	 * will be stored under the key "Status".
//	 *
//	 * @param headers - HTTP/1.1 header lines
//	 * @return field names mapped to values if the headers are properly formatted
//	 */
//	public static Map<String, String> parseHeaders(List<String> headers) {
//        Map<String, String> fields = new HashMap<>();
//
//        if (headers.size() > 0 && headers.get(0).startsWith(version)) {
//            fields.put("Status", headers.get(0).substring(version.length()).trim());
//
//            for (String line : headers.subList(1, headers.size())) {
//                String[] pair = line.split(":", 2);
//
//                if (pair.length == 2) {
//                    fields.put(pair[0].trim(), pair[1].trim());
//                }
//            }
//        }
//        return fields;
//	}
    


	/**
     * Removes everything between the element tags, and the element tags
     * themselves. For example, consider the html code:
     *
     * <pre>
     * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
     * </pre>
     *
     * If removing the "style" element, all of the above code will be removed,
     * and replaced with the empty string.
     *
     * @param name
     *            name of the element to strip, like "style" or "script"
     * @param html
     *            html code to parse
     * @return html code without the element specified
     */
    public static String stripElement(String name, String html) {
    	html = html.replaceAll("(?i)(?s)<" + name + ".*?[^<]*<.*?" + name + ".*?>", "");
        return html;
    }

    /**
     * Removes all HTML tags, which is essentially anything between the "<" and
     * ">" symbols. The tag will be replaced by the empty string.
     *
     * @param html
     *            html code to parse
     * @return text without any html tags
     */
    public static String stripTags(String html) {
    	html = html.replaceAll("<[^>]*>", "");
        return html;
    }

    /**
     * Replaces all HTML entities in the text with an empty string. For example,
     * "2010&ndash;2012" will become "20102012".
     *
     * @param html
     *            the text with html code being checked
     * @return text with HTML entities replaced by an empty string
     */
    public static String stripEntities(String html) {
    	html = html.replaceAll("(?is)&[^\\s]*?;", "");
        return html;
    }

}

