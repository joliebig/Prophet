package experimentGUI.plugins;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import experimentGUI.Constants;
import experimentGUI.PluginInterface;
import experimentGUI.experimentViewer.ExperimentViewer;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;
import experimentGUI.util.settingsComponents.SettingsPluginComponentDescription;
import experimentGUI.util.settingsComponents.components.SettingsCheckBox;
import experimentGUI.util.settingsComponents.components.SettingsFilePathChooser;
import experimentGUI.util.settingsComponents.components.SettingsTextArea;

public class ValidSubjectCodePlugin implements PluginInterface {

	private static final String KEY = "valid_code";
	private static final String KEY_CODES = "codes";
	private static final String KEY_PATH = "path";
	private static final String KEY_IGNORE_CASE = "ignorecase";

	@Override
	public SettingsComponentDescription getSettingsComponentDescription(
			QuestionTreeNode node) {
		if (node.isExperiment()) {
			SettingsPluginComponentDescription result = new SettingsPluginComponentDescription(
					KEY, "Auf Probandencode überprüfen", true);
			result.addSubComponent(new SettingsComponentDescription(
					SettingsTextArea.class, KEY_CODES,
					"gültige Codes (optional)"));
			result.addSubComponent(new SettingsComponentDescription(
					SettingsFilePathChooser.class, KEY_PATH,
					"Probandencodedatei (optional)"));
			result.addSubComponent(new SettingsComponentDescription(
					SettingsCheckBox.class, KEY_IGNORE_CASE,
					"Groß- und Kleinschreibung ignorieren"));
			return result;
		}
		return null;
	}

	@Override
	public void experimentViewerRun(ExperimentViewer experimentViewer) {
	}

	@Override
	public boolean denyEnterNode(QuestionTreeNode node) {
		return false;
	}

	@Override
	public void enterNode(QuestionTreeNode node) {
	}

	@Override
	public String denyNextNode(QuestionTreeNode currentNode) {
		if (!currentNode.isExperiment()) {
			return null;
		} else if (Boolean.parseBoolean(currentNode.getAttributeValue(KEY))) {
			String subjectCode = currentNode.getAnswer(Constants.KEY_SUBJECT);
			boolean ignoreCase = Boolean.parseBoolean(currentNode.getAttribute(
					KEY).getAttributeValue(KEY_IGNORE_CASE));
			if (ignoreCase) {
				subjectCode = subjectCode.toLowerCase();
			}

			String codes = currentNode.getAttribute(KEY).getAttributeValue(
					KEY_CODES);
			if (codes != null && !codes.equals("")) {
				Scanner sc = new Scanner(codes);
				while (sc.hasNext()) {
					String line = sc.next();
					if (ignoreCase) {
						line = line.toLowerCase();
					}
					if (subjectCode.equals(line)) {
						return null;
					}
				}
			}

			String path = currentNode.getAttribute(KEY).getAttributeValue(
					KEY_PATH);
			if (path != null && !path.equals("")) {
				try {
					Scanner sc = new Scanner(new FileReader(path));
					while (sc.hasNext()) {
						String line = sc.next();
						if (ignoreCase) {
							line = line.toLowerCase();
						}
						if (subjectCode.equals(line)) {
							return null;
						}
					}
				} catch (FileNotFoundException e) {
					return "Datei mit gültigen Probandencodes nicht gefunden.";
				}
			}
			return "Probandencode nicht gefunden.";
		} else {
			return null;
		}
	}

	@Override
	public void exitNode(QuestionTreeNode node) {
	}

	@Override
	public String finishExperiment() {
		return null;
	}
}
