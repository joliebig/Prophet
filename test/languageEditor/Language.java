package test.languageEditor;

import java.util.TreeMap;


/**
 * this class can load language specifications from xml files you can set the
 * language and get - per method your desired notes which correspond to a
 * keyword
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 */
public class Language {

	//contains the keywords and the corresponding notes
	private static TreeMap<String, TreeMap<String, String>> keywords;
	private TreeMap<String, TreeMap<String, String>> localKeywords;
	//contains the current chose language
	private static String currentLanguage;
	@SuppressWarnings("unused")
	private String currentLocalLanguage;
	//fallback language
	private static String fallbackLanguage;
	@SuppressWarnings("unused")
	private String localFallbackLanguage;

	private static final String ERROR_MSG = "not found";

	/**
	 * xml constants
	 */
	public static final String ELEMENT_LANGUAGES = "languages";
	public static final String ELEMENT_LANGUAGE = "language";
	public static final String ELEMENT_KEYWORD = "keyword";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ELEMENT_KEY_LAN = "languageInterpretation";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	public static final String ATTRIBUTE_INTERPRETATION = "interpretation";

	public Language() {
		localKeywords = new TreeMap<String, TreeMap<String, String>>();
	}

	public String put(String language, String key, String value) {
		if(localKeywords.containsKey(language)) {
			return localKeywords.get(language).put(key, value);
		} else {
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.put(key, value);
			localKeywords.put(language, treeMap);
			return value;
		}
	}

	public String getValue(String language, String key) {
		if(localKeywords.containsKey(language)) {
			return localKeywords.get(language).get(key);
		} else {
			return null;
		}
	}

	public void setLocalLanguage(String language) {
		currentLocalLanguage = language;
	}

	public void setLocalFallbackLanguage(String language) {
		localFallbackLanguage = language;
	}



	/**
	 * initialize the data with the content of an xml file
	 *
	 * @param path
	 *            path of the xml file
	 */
	public static void init(String path) {
		Pair<TreeMap<String, TreeMap<String, String>>, String> data = LanguageXMLHandler.load(path);
		keywords = data.getKey();
		fallbackLanguage = data.getValue();
	}

	/**
	 * sets the language
	 *
	 * @param language
	 *            name of the language
	 * @return true if applying was successful
	 */
	public static boolean setLanguage(String language) {
		if (keywords.get(language) != null) {
			currentLanguage = language;
			return true;
		} else {
			currentLanguage = fallbackLanguage;
			return false;
		}
	}

	/**
	 * returns the String representation of the currently used language
	 *
	 * @return current used language
	 */
	public static String getLanguage() {
		return currentLanguage;
	}

	/**
	 * returns the note to a specific keyword in the current language
	 *
	 * @param key
	 *            keyword to the corresponding note
	 * @return note to the corresponding keyword (or null if not found)
	 */
	public static String getValue(String key) {
		TreeMap<String, String> specifications = keywords.get(currentLanguage);
		if(specifications == null) {
			return ERROR_MSG;
		}
		String interpretation = specifications.get(key);
		if (interpretation == null) {
			return ERROR_MSG;
		} else {
			return interpretation;
		}
	}
}
