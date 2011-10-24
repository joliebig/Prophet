package test.experimentEditorDataVisualizer;

/**
 * File which contains useful conversion methods
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 */
public class Conversion {

	/**
	 * converts a (Linux) file path to a Windows file path, if the program runs
	 * on a Windows System
	 *
	 * @param path
	 *            path which should be converted
	 * @return windows path
	 */
	public static String toWindowsPath(String path) {
		if (path != null && System.getProperty("file.separator").equals("\\")) {
			return path.replaceAll("/", "\\");
		}
		return path;
	}
}
