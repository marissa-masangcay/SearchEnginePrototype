import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {

		/** Stores arguments in a map, where the key is a flag. */
		private Map<String, String> argumentMap;

		/**
		 * Initializes an empty argument map. The method {@link #parseArguments(String[])}
		 * should eventually be called to populate the map.
		 * @return 
		 */
		public ArgumentParser() {
			argumentMap = new HashMap<>();
		}

		/**
		 * Initializes the argument map with the provided command-line arguments.
		 * Uses {@link #parseArguments(String[])} to populate the map.
		 *
		 * @param args command-line arguments
		 * @return 
		 * @see #parseArguments(String[])
		 */
		public ArgumentParser(String[] args) {
			this();
			parseArguments(args);
		}

		/**
		 * Iterates through the array of command-line arguments. If a flag is
		 * found, will attempt to see if it is followed by a value. If so, the
		 * flag/value pair is added to the map. If the flag is followed by
		 * another flag, then the first flag is added to the map with a null
		 * value.
		 *
		 * @param args command-line arguments
		 *
		 * @see #isFlag(String)
		 * @see #isValue(String)
		 */
		public void parseArguments(String[] args) {
			for (int i = 0; i < args.length; i++)
			{			
				if(args[i].startsWith("-"))
				{
					if(i==args.length-1)
					{
						argumentMap.put(args[i], null);
					}
					else
					{
						if(!args[i+1].startsWith("-"))
						{
							argumentMap.put(args[i], args[i+1]);
							i++;
						}
						
						else if(args[i+1].startsWith("-"))
						{
							argumentMap.put(args[i], null);
						}
					}
				}
			}
		}

		/**
		 * Tests if the provided argument is a flag by checking that it starts with
		 * a "-" dash symbol, and is followed by at least one non-whitespace
		 * character. For example, "-a" and "-1" are valid flags, but "-" and "- "
		 * are not valid flags.
		 *
		 * @param arg command-line argument
		 * @return true if the argument is a flag
		 */
		public static boolean isFlag(String arg) {
			String trimmedArg = arg.trim();
			if(trimmedArg.startsWith("-") && trimmedArg.length()>1)
			{
				return true;
			}
			else
			return false;
		}

		/**
		 * Tests if the provided argument is a value by checking that it does not
		 * start with a "-" dash symbol, and contains at least one non-whitespace
		 * character. For example, "a" and "1" are valid values, but "-" and " "
		 * are not valid values.
		 *
		 * @param arg command-line argument
		 * @return true if the argument is a value
		 */
		public static boolean isValue(String arg) {
			String trimmedArg = arg.trim();
			if(!trimmedArg.startsWith("-") && !trimmedArg.isEmpty())
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		/**
		 * Returns the number of flags stored.
		 *
		 * @return number of flags
		 */
		public int numFlags() {
			int numberOfFlags = argumentMap.size();
			return numberOfFlags;
		}

		/**
		 * Tests if the provided flag is stored in the map.
		 *
		 * @param flag flag to check
		 * @return value if flag exists and has a value, or null if the flag
		 * does not exist or does not have a value
		 */
		public boolean hasFlag(String flag) {
			if(argumentMap.containsKey(flag))
			{
				return true;
			}
			
			else
			{
				return false;
			}
		}

		/**
		 * Tests if the provided flag has a non-empty value.
		 *
		 * @param flag flag to check
		 * @return true if the flag exists and has a non-null non-empty value
		 */
		public boolean hasValue(String flag) {
			if(argumentMap.containsKey(flag))
			{
				String flagValue = argumentMap.get(flag);
				if(flagValue != null)
				{
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Returns the value of a flag if it exists, and null otherwise.
		 *
		 * @param flag flag to check
		 * @return value of flag or null if flag does not exist or has no value
		 */
		public String getValue(String flag) {
			if(argumentMap.containsKey(flag))
			{
				return argumentMap.get(flag);
			}
			else
			{
				return null;
			}
		}

		@Override
		public String toString() {
			return argumentMap.toString();
		}

}
