package experimentGUI.experimentEditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import experimentGUI.Constants;
import experimentGUI.experimentEditor.tabbedPane.editorTabs.ContentEditorPanel;
import experimentGUI.util.Pair;
import experimentGUI.util.questionTreeNode.QuestionTreeHTMLHandler;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.questionTreeNode.QuestionTreeXMLHandler;

/**
 * The menu bar of the ExperimentViewer. Separated to enhance readability.
 *
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
public class ExperimentEditorMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	/**
	 * String constants for the menu(items)
	 */
	public final static String MENU_FILE = "Datei";
	public final static String MENU_FILE_NEW = "Neu";
	public final static String MENU_FILE_OPEN = "Laden";
	public final static String MENU_FILE_SAVE = "Speichern";
	public final static String MENU_FILE_SAVE_AS = "Speichern unter...";
	public final static String MENU_FILE_QUIT = "Beenden";

	public final static String MENU_EDIT = "Bearbeiten";
	public final static String MENU_EDIT_FIND = "Suchen";

	/**
	 * String constants for (error) messages
	 */
	public final static String MESSAGE_FILE_NOT_FOUND = "Datei nicht gefunden";
	public final static String MESSAGE_FILE_NOT_FOUND_TITLE = "Fehler";
	public final static String MESSAGE_REPLACE_FILE = " ist bereits vorhanden.\nWollen Sie sie ersetzen?";
	public final static String MESSAGE_REPLACE_FILE_TITLE = "Speichern unter bestätigen";

	private ExperimentEditor experimentEditor;
	private File currentFile;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem nameCheckMenuItem;
	private JMenuItem exportCSVMenuItem;
	private JMenu exportMenu;

	/**
	 * Constructor
	 *
	 * @param eE
	 *            The ExperimentEditor object this menu bar is added to
	 */
	public ExperimentEditorMenuBar(final ExperimentEditor eE) {
		experimentEditor = eE;
		currentFile = null;

		//DATEI

		JMenu fileMenu = new JMenu(MENU_FILE);
		add(fileMenu);

		JMenuItem newMenuItem = new JMenuItem(MENU_FILE_NEW);
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		fileMenu.add(newMenuItem);
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				currentFile = null;
				experimentEditor.setTitle(ExperimentEditor.TITLE);
				experimentEditor.newTree();

				enableMenuItems();
			}
		});

		JMenuItem loadMenuItem = new JMenuItem(MENU_FILE_OPEN);
		loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
		fileMenu.add(loadMenuItem);
		loadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(currentFile == null ? new File(".") : currentFile);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					currentFile = fc.getSelectedFile();
					try {
						QuestionTreeNode myTree = QuestionTreeXMLHandler.loadXMLTree(currentFile.getAbsolutePath());
						if(myTree!=null) {
							experimentEditor.loadTree(myTree);
							experimentEditor.setTitle(ExperimentEditor.TITLE + " - "
									+ currentFile.getAbsolutePath());

							enableMenuItems();
						} else {
							JOptionPane.showMessageDialog(experimentEditor, "Keine gültige Experiment-Datei.");
						}

					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(experimentEditor, MESSAGE_FILE_NOT_FOUND,
								MESSAGE_FILE_NOT_FOUND_TITLE, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});

		saveMenuItem = new JMenuItem(MENU_FILE_SAVE);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		fileMenu.add(saveMenuItem);
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (currentFile != null) {
					experimentEditor.getTabbedPane().save();
					QuestionTreeXMLHandler.saveXMLTree(experimentEditor.getTreeComponent().getRoot(),
							currentFile.getAbsolutePath());
				} else {
					saveAsMenuItem.doClick();
				}
			}
		});

		saveAsMenuItem = new JMenuItem(MENU_FILE_SAVE_AS);
		saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		fileMenu.add(saveAsMenuItem);
		saveAsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(currentFile);
				if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.exists()) {
						int n = JOptionPane.showConfirmDialog(null, file.getName() + MESSAGE_REPLACE_FILE,
								MESSAGE_REPLACE_FILE_TITLE, JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.NO_OPTION) {
							return;
						}
					}
					currentFile = file;
					QuestionTreeXMLHandler.saveXMLTree(experimentEditor.getTreeComponent().getRoot(),
							file.getAbsolutePath());
					experimentEditor.setTitle(ExperimentEditor.TITLE + " - " + currentFile.getAbsolutePath());
					enableMenuItems();
				}
			}
		});

		fileMenu.addSeparator();

		exportMenu = new JMenu("Exportieren");
		fileMenu.add(exportMenu);

		JMenuItem exportHTMLFileMenuItem = new JMenuItem("HTML-Datei der Fragen");
		exportMenu.add(exportHTMLFileMenuItem);
		exportHTMLFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(currentFile);
				if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.exists()) {
						int n = JOptionPane.showConfirmDialog(null, file.getName() + MESSAGE_REPLACE_FILE,
								MESSAGE_REPLACE_FILE_TITLE, JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.NO_OPTION) {
							return;
						}
					}
					QuestionTreeHTMLHandler.saveAsHTMLFile(file, experimentEditor.getTreeComponent().getRoot());
				}
			}
		});

		exportCSVMenuItem = new JMenuItem("CSV-Datei der Antworten");
		exportMenu.add(exportCSVMenuItem);
		exportCSVMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					QuestionTreeNode experimentNode = experimentEditor.getTreeComponent().getRoot();
					ArrayList<Pair<QuestionTreeNode, ArrayList<Pair<String, String>>>> formInfos = QuestionTreeHTMLHandler
							.getForms(experimentNode);
					ArrayList<QuestionTreeNode> answerNodes = new ArrayList<QuestionTreeNode>();
					String experimentCode = experimentNode.getAttributeValue(Constants.KEY_EXPERIMENT_CODE);
					// Antwortdateien ermitteln
					String path = currentFile.getCanonicalPath();
					int index = path.lastIndexOf(System.getProperty("file.separator"));
					path = index != -1 ? path.substring(0, index) : path;
					File f = new File(path);
					getAnswerFiles(f, answerNodes, experimentCode, true);
					// csv Datei erstellen
					QuestionTreeXMLHandler.saveAsCSVFile(formInfos, answerNodes, experimentCode, path);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Fehler bei der Ermittlung des Pfades der geladenen Datei.");
				}
			}
		});

		fileMenu.addSeparator();

		JMenuItem closeMenuItem = new JMenuItem(MENU_FILE_QUIT);
		fileMenu.add(closeMenuItem);

		// BEARBEITEN

		JMenu editMenu = new JMenu(MENU_EDIT);
		add(editMenu);

		newMenuItem = new JMenuItem(MENU_EDIT_FIND);
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		editMenu.add(newMenuItem);
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Component current = eE.getTabbedPane().getSelectedComponent();
				if (current instanceof ContentEditorPanel) {
					((ContentEditorPanel)current).search();
				}
			}
		});

		// EXTRAS

		JMenu extrasMenu = new JMenu("Extras");
		add(extrasMenu);

		nameCheckMenuItem = new JMenuItem("Überprüfung Formularnamen");
		extrasMenu.add(nameCheckMenuItem);
		nameCheckMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String globalOutput = "";
				// get infos
				QuestionTreeNode experimentNode = experimentEditor.getTreeComponent().getRoot();
				ArrayList<Pair<QuestionTreeNode, ArrayList<Pair<String, String>>>> formInfos = QuestionTreeHTMLHandler
						.getForms(experimentNode);

				for (Pair<QuestionTreeNode, ArrayList<Pair<String, String>>> nodeInfo : formInfos) {
					String output = "<b>" + nodeInfo.getKey().getName() + ":</b><br>";
					boolean add = false;
					ArrayList<Pair<String, String>> forms = nodeInfo.getValue();
					for (int i = 0; i < forms.size() - 1; i++) {
						Pair<String, String> currentForm = forms.get(i);
						int appearances = 1;
						// anzahl der vorkommen des derzeitigen
						// formularelementes berechnen
						for (int j = i + 1; j < forms.size(); j++) {
							if (currentForm.getKey() != null
									&& currentForm.getKey().equals(forms.get(j).getKey())) {
								if ((currentForm.getValue() != null && currentForm.getValue().equals(
										forms.get(j).getValue()))
										|| currentForm.getValue() == null && forms.get(j).getValue() == null) {
									appearances++;
									forms.remove(forms.get(j));
									j--;
								}
							}
						}
						if (appearances > 1) {
							output += "Name = " + currentForm.getKey() + (currentForm.getValue() != null ? "; Value = "
									+ currentForm.getValue() : "; kein Value") + "; Vorkommen = " + appearances + "<br>";
							add=true;
						}
					}
					globalOutput += add ? output+"<br>" : "";
				}

				JOptionPane.showMessageDialog(null,
						globalOutput.length()>0 ? "<html>Auflistung der Formularkomponenten mit gleichem name- und value-Attribut in einem Knoten:<br><br>"
								+ globalOutput + "</html>" : "Keine Duplikate gefunden.");
			}
		});

		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		enableMenuItems();
	}

	/**
	 * Search for answer.xml files in the directory of file and its subdirectories.
	 * Loads the Data from this files and saves it to the answerNodes-ArrayList.
	 * Select directories after name (if the correct experiment code is used)
	 * @param file
	 *  file or directory in which is searched
	 * @param answerNodes
	 *  storage for the data from the answer.xml files
	 * @param experimentCode
	 *  used experiment code
	 * @param search
	 *  if false the answer.xml files of this directory are not used - but the files of the subdirectories are still used
	 */
	private void getAnswerFiles(File file, ArrayList<QuestionTreeNode> answerNodes, String experimentCode,
			boolean search) {
		File[] directoryFiles = file.listFiles();
		for (int i = 0; i < directoryFiles.length; i++) {
			File currentFile = directoryFiles[i];
			if (currentFile.isDirectory()) {
				if (currentFile.getName().startsWith(experimentCode + "_")) {
					getAnswerFiles(currentFile, answerNodes, experimentCode, true);
				} else {
					getAnswerFiles(currentFile, answerNodes, experimentCode, false);
				}
			} else if (search && currentFile.getName().equals("answers.xml")) {
				try {
					QuestionTreeNode node = QuestionTreeXMLHandler.loadAnswerXMLTree(currentFile.getPath());
					if(node != null) {
						answerNodes.add(node);
					}
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Datei " + currentFile.getAbsolutePath()
							+ " nicht gefunden.");
				}
			}
		}
	}

	/**
	 * (de)activate the menu items matching to the current state
	 */
	private void enableMenuItems() {
		if (experimentEditor.getTreeComponent()!=null && experimentEditor.getTreeComponent().getRoot()!=null) {
			if(currentFile == null) {
				exportCSVMenuItem.setEnabled(false);
			} else {
				exportCSVMenuItem.setEnabled(true);
			}
			saveMenuItem.setEnabled(true);
			saveAsMenuItem.setEnabled(true);
			exportMenu.setEnabled(true);
			nameCheckMenuItem.setEnabled(true);
		} else {
			saveMenuItem.setEnabled(false);
			saveAsMenuItem.setEnabled(false);
			exportMenu.setEnabled(false);
			nameCheckMenuItem.setEnabled(false);
		}
	}
}
