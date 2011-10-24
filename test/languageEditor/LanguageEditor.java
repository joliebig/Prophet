package test.languageEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This ist a little programm to create/manipulate a xml File, which could be
 * used from language.java
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 */
public class LanguageEditor extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 6261884188595827286L;
	/**
	 * standard language constant
	 */
	public static String LANGUAGE_GERMAN = "de";
	public static String DEFAULT_LANGUAGE_UNDEFINED = "Ausweichsprache setzen";
	public static String DEFAULT_LANGUAGE_DEFINED = "Ausweichsprache: ";

	private JList list;
	private DefaultListModel listModel;
	private JTextField textField;
	private JTextArea textArea;
	private JComboBox comboBox;
	private DefaultComboBoxModel boxModel;
	private JTextField lanTextField;
	private JButton removeLanButton;
	private JMenuItem loadMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem closeMenuItem;
	private JButton addButton;
	private JButton removeButton;
	private JButton addLanButton;
	private JMenuItem saveAsMenuItem;
	private JMenuItem newMenuItem;
	private JButton fallbackLanguageButton;

	/**
	 * the current path for the file to save in
	 */
	private String path;
	/**
	 * structure to save the keyword and the corresponding words/sentences the
	 * order of these words is the same as the language order
	 */
	private TreeMap<String, TreeMap<String, String>> keywords;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String laf = UIManager.getSystemLookAndFeelClassName();
					UIManager.setLookAndFeel(laf);

					LanguageEditor frame = new LanguageEditor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creating the GUI and initializing
	 */
	public LanguageEditor() {
		// initialise the data structures
		keywords = new TreeMap<String, TreeMap<String, String>>();
		path = null;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("Datei");
		menuBar.add(fileMenu);
		saveMenuItem = new JMenuItem("Speichern");

		newMenuItem = new JMenuItem("Neu");
		fileMenu.add(newMenuItem);
		loadMenuItem = new JMenuItem("Laden");
		fileMenu.add(loadMenuItem);
		fileMenu.add(saveMenuItem);

		saveAsMenuItem = new JMenuItem("Speichern unter");
		fileMenu.add(saveAsMenuItem);
		closeMenuItem = new JMenuItem("Beenden");
		fileMenu.add(closeMenuItem);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		addButton = new JButton("Hinzufügen");
		textField = new JTextField();
		textField.setColumns(10);
		removeButton = new JButton("Entfernen");

		listModel = new DefaultListModel();
		list = new JList();
		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(textArea, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panel_2, BorderLayout.NORTH);

		comboBox = new JComboBox();
		boxModel = new DefaultComboBoxModel();
		comboBox.setModel(boxModel);
		panel_2.add(comboBox);

		lanTextField = new JTextField();
		panel_2.add(lanTextField);
		lanTextField.setColumns(10);

		addLanButton = new JButton("Neue Sprache");
		addLanButton.setToolTipText("ISO 639-1 Sprachangabe");
		panel_2.add(addLanButton);
		removeLanButton = new JButton("Sprache entfernen");
		panel_2.add(removeLanButton);

		fallbackLanguageButton = new JButton(DEFAULT_LANGUAGE_UNDEFINED);
		panel_2.add(fallbackLanguageButton);

		JPanel keywordPanel = new JPanel();
		keywordPanel.setLayout(new BorderLayout());

		keywordPanel.add(new JScrollPane(list), BorderLayout.CENTER);
		JPanel keywordMenuPanel = new JPanel();
		keywordMenuPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		keywordMenuPanel.setLayout(new GridLayout(2, 1));
		JPanel addKeywordPanel = new JPanel();
		addKeywordPanel.add(textField);
		addKeywordPanel.add(addButton);
		keywordMenuPanel.add(addKeywordPanel);
		JPanel removeKeyPanel = new JPanel();
		removeKeyPanel.add(removeButton);
		keywordMenuPanel.add(removeKeyPanel);
		keywordPanel.add(keywordMenuPanel, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(keywordPanel);
		splitPane.setRightComponent(contentPanel);
		contentPane.add(splitPane, BorderLayout.CENTER);

		setListener();
		newMenuItem.doClick();
	}

	/**
	 * set all Listeners and define their methods
	 */
	private void setListener() {
		// close program
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		// new language specification
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				path = null;
				keywords.clear();
				keywords.put(LANGUAGE_GERMAN, new TreeMap<String, String>());
				showInfos();
			}
		});
		// save current language specification
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (path == null) {
					saveAsMenuItem.doClick();
				} else {
					String defaultLanguage = fallbackLanguageButton.getText();
					defaultLanguage = defaultLanguage.equals(DEFAULT_LANGUAGE_UNDEFINED) ? null
							: defaultLanguage.substring(DEFAULT_LANGUAGE_DEFINED.length());
					LanguageXMLHandler.save(path, keywords, defaultLanguage);
				}
			}
		});
		// save-as option
		saveAsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (keywords.size() == 0) {
					JOptionPane.showMessageDialog(null,
							"Keine Sprache vorhanden. Datei wird nicht gespeichert.");
				} else {
					JFileChooser fileChooser = new JFileChooser();
					int n = fileChooser.showSaveDialog(null);
					if (n == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						if (file.exists()) {
							n = JOptionPane.showConfirmDialog(null, "Datei ersetzen?",
									"Wollen Sie die Datei " + file.getName() + "wirklich ersetzen?",
									JOptionPane.YES_NO_OPTION);
							if (n == JOptionPane.YES_OPTION) {
								path = file.getAbsolutePath();
								String defaultLanguage = fallbackLanguageButton.getText();
								defaultLanguage = defaultLanguage.equals(DEFAULT_LANGUAGE_UNDEFINED) ? null
										: defaultLanguage.substring(DEFAULT_LANGUAGE_DEFINED.length());
								LanguageXMLHandler.save(path, keywords, defaultLanguage);
							}
						}
					}
				}
			}
		});
		// load language specification
		loadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				int n = fileChooser.showOpenDialog(null);
				if (n == JFileChooser.APPROVE_OPTION) {
					path = fileChooser.getSelectedFile().getAbsolutePath();
					Pair<TreeMap<String, TreeMap<String, String>>, String> data = LanguageXMLHandler
							.load(path);
					keywords = data.getKey();
					String defaultLanguage = data.getValue();
					if (defaultLanguage != null) {
						fallbackLanguageButton.setText(DEFAULT_LANGUAGE_DEFINED + defaultLanguage);
					} else {
						fallbackLanguageButton.setText(DEFAULT_LANGUAGE_UNDEFINED);
					}
					showInfos();
				}
			}
		});
		// add language
		addLanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String lanName = lanTextField.getText();
				lanTextField.setText("");
				lanTextField.grabFocus();
				if (!lanName.equals("")) {
					keywords.put(lanName, new TreeMap<String, String>());
					boxModel.addElement(lanName);
					comboBox.setSelectedItem(lanName);

					testEnabling();
				}
			}
		});
		// remove language
		removeLanButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (boxModel.getSize() == 1) {
					int lanIndex = comboBox.getSelectedIndex();
					String lanName = comboBox.getSelectedItem().toString();
					keywords.remove(lanName);
					boxModel.removeElementAt(lanIndex);
					testEnabling();
					lanTextField.grabFocus();
				} else {
					int lanIndex = comboBox.getSelectedIndex();
					String lanName = comboBox.getSelectedItem().toString();
					keywords.remove(lanName);
					boxModel.removeElementAt(lanIndex);
					list.clearSelection();
					textArea.setText("");
					textArea.setEnabled(false);
					comboBox.setSelectedIndex(lanIndex - 1);
				}
			}
		});
		// default language
		fallbackLanguageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox.getSelectedIndex() != -1) {
					fallbackLanguageButton.setText(DEFAULT_LANGUAGE_DEFINED
							+ comboBox.getSelectedItem().toString());
				}
			}
		});
		// add keyword
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String keyword = getFreeName(textField.getText());
				if (!keyword.equals("")) {
					removeButton.setEnabled(true);
					String lanName = comboBox.getSelectedItem().toString();
					keywords.get(lanName).put(keyword, "");
					listModel.addElement(keyword);
					textField.setText("");
					list.setSelectedValue(keyword, true);
					textArea.grabFocus();
				}
			}
		});
		// remove keyword
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int index = list.getSelectedIndex();
				if (index != -1) {
					String lanName = comboBox.getSelectedItem().toString();
					String key = listModel.get(index).toString();
					keywords.get(lanName).remove(key);
					listModel.remove(index);
					if (index > 0) {
						list.setSelectedIndex(index - 1);
					} else {
						removeButton.setEnabled(false);
						list.clearSelection();
						textField.grabFocus();
					}
				}
			}
		});
		// list selection
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int listIndex = list.getSelectedIndex();
				if (listIndex != -1) {
					String lanName = comboBox.getSelectedItem().toString();
					textArea.setEnabled(true);
					textArea.setText(keywords.get(lanName).get(listModel.elementAt(listIndex)));
					textArea.grabFocus();
				} else {
					textArea.setEnabled(false);
					textArea.setText("");
				}
			}
		});
		// comboBox selection
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				String lanName = (String) comboBox.getSelectedItem();
				if (lanName != null) {
					listModel.clear();
					Iterator<String> keywordIterator = keywords.get(lanName).keySet().iterator();
					while (keywordIterator.hasNext()) {
						listModel.addElement(keywordIterator.next());
					}
					testEnabling();
				}
			}
		});
		// save changes in textarea
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			private void valueChanged() {
				int keyIndex = list.getSelectedIndex();
				if (keyIndex != -1) {
					String lanName = comboBox.getSelectedItem().toString();
					String key = listModel.getElementAt(keyIndex).toString();
					keywords.get(lanName).put(key, textArea.getText());
				}
			}

			public void changedUpdate(DocumentEvent arg0) {
				valueChanged();
			}

			public void insertUpdate(DocumentEvent arg0) {
				valueChanged();
			}

			public void removeUpdate(DocumentEvent arg0) {
				valueChanged();
			}
		});
		// Type in keyword-textfield
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addButton.doClick();
				}
			}
		});
	}

	/**
	 * shows the current data in the JList and JComboBox Clears the JList
	 * selection and the JTextArea
	 */
	private void showInfos() {
		boxModel.removeAllElements();

		Iterator<String> languageIterator = keywords.keySet().iterator();
		while (languageIterator.hasNext()) {
			boxModel.addElement(languageIterator.next());
		}
		comboBox.setSelectedIndex(0);
		String lanName = comboBox.getSelectedItem().toString();
		list.clearSelection();
		listModel.removeAllElements();
		Iterator<String> keywordIterator = keywords.get(lanName).keySet().iterator();
		while (keywordIterator.hasNext()) {
			String keyword = keywordIterator.next();
			listModel.addElement(keyword);
		}
		testEnabling();
	}

	private void testEnabling() {
		if (keywords.size() != 0) {
			removeLanButton.setEnabled(true);
			fallbackLanguageButton.setEnabled(true);
			addButton.setEnabled(true);
			if (keywords.get(comboBox.getSelectedItem()).size() != 0) {
				list.setSelectedIndex(0);
				textArea.setEnabled(true);
				removeButton.setEnabled(true);
			} else {
				removeButton.setEnabled(false);
				textArea.setText("");
				textArea.setEnabled(false);
			}
		} else {
			removeLanButton.setEnabled(false);
			fallbackLanguageButton.setEnabled(false);
			addButton.setEnabled(false);
			removeButton.setEnabled(false);
			textArea.setText("");
			textArea.setEnabled(false);
		}
	}

	/**
	 * gets the next free name for a keyword by appending a "'" if needed
	 *
	 * @param name
	 *            keyword name which is wanted
	 * @return next free keyword name
	 */
	private String getFreeName(String name) {
		if (name.equals("")) {
			return getFreeName("default");
		} else if (listModel.contains(name)) {
			return getFreeName(name + "'");
		} else {
			return name;
		}
	}
}