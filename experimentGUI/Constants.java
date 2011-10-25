package experimentGUI;

/**
 * Class used to store constants used throughout the code
 * 
 * @author Andreas Hasselberg
 * @author Markus Köppen
 * 
 */
public class Constants {
	/**
	 * File name of the experiment file opened automatically when program starts
	 */
	public static final String DEFAULT_FILE = "default.xml";
	/**
	 * File name of the file answers are saved in
	 */
	public static final String FILE_ANSWERS = "answers.xml";
	/**
	 * property name for categories whose content isn't shown and the first
	 * question within is started immediately
	 */
	public static final String KEY_DONOTSHOWCONTENT = "donotshowcontent";
	/**
	 * property name for categories, determines if subjects may go back to
	 * previous questions within that category
	 */
	public static final String KEY_QUESTIONSWITCHING = "questionswitching";

	/**
	 * property name for the experiment code (which is used for online usage)
	 */
	public static final String KEY_EXPERIMENT_CODE = "experimentcode";

	// constants for names in html-files - DO NOT USE
	/**
	 * html name for the button to the next question
	 */
	public static final String KEY_FORWARD = "nextQuestion";
	/**
	 * html name for the button to the previous question (only shown if allowed)
	 */
	public static final String KEY_BACKWARD = "previousQuestion";

	/**
	 * html name for the input field used to enter the subject code
	 */
	public static final String KEY_SUBJECT = "subjectcode";
}
