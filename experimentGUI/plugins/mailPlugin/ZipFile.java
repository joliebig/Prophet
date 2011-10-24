package experimentGUI.plugins.mailPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * this class contains zip methods
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 */
public class ZipFile {

	private static ZipOutputStream zipOut;
	private static File outputFile;

	/**
	 * a main method to test the zip-method
	 * @param args
	 */
	public static void main(String[] args) {
		zipFiles(new File("."), new File("cow.zip"));
	}

	/**
	 * zips a file or directory, ignores zip-files
	 * @param zipPath path to the file or directory which should be zipped
	 * @param outputName name of the zip file which is created
	 */
	public static void zipFiles(File zipPath, File outputF) {
		try {
			outputFile=outputF;
			outputFile.getAbsoluteFile().getParentFile().mkdirs();
			zipOut = new ZipOutputStream(new FileOutputStream(outputFile));
			zipDir(zipPath);
			zipOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to zip a directory
	 * @param zipDir the directory
	 */
	private static void zipDir(File zipDir) {
		try {
			zipDir = zipDir.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		zipDir(zipDir,zipDir.getName());
	}

	/**
	 * method to zip a directory
	 * @param zipDir the directory
	 */
	private static void zipDir(File zipDir, String path) {
		File[] fileArray = zipDir.listFiles();
		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isDirectory()) {
				zipDir(fileArray[i], path + System.getProperty("file.separator") + fileArray[i].getName());
			} else {
				zipFile(fileArray[i], path + System.getProperty("file.separator") + fileArray[i].getName());
			}
		}
	}

	/**
	 * method to zip a file
	 * @param file the file
	 */
	private static void zipFile(File file, String path) {
		byte[] buf = new byte[4096];
		if (file.getAbsolutePath().equals(outputFile.getAbsolutePath())) return;
		try {
			FileInputStream inFile = new FileInputStream(file);
			zipOut.putNextEntry(new ZipEntry(path));
			int len;
			while ((len = inFile.read(buf)) > 0) {
				zipOut.write(buf, 0, len);
			}
			inFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
