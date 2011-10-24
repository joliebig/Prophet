package experimentGUI.util.miniEditors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import test.languageEditor.Pair;

import javax.swing.border.LineBorder;
import java.awt.Color;

/**
 * A simple editor to create and change the macro.xml
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 */
public class MacroEditor extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JList macroList;
	private DefaultListModel listModel;
	private JTextPane macroContentTextPane;
	private JTextField macroNameTextField;

	private ArrayList<Pair<String, String>> macros;

	/**
	 * Main method - launch application
	 *
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MacroEditor frame = new MacroEditor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor which creates GUI and sets listener
	 */
	public MacroEditor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 491, 300);
		JMenuBar macroMenuBar = new JMenuBar();
		setJMenuBar(macroMenuBar);
		JMenu macroMenu = new JMenu("Datei");
		macroMenuBar.add(macroMenu);
		JMenuItem saveMenuItem = new JMenuItem("Speichern");
		macroMenu.add(saveMenuItem);
		JMenuItem closeMenuItem = new JMenuItem("Beenden");
		macroMenu.add(closeMenuItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		JPanel overviewPanel = new JPanel();
		contentPane.add(overviewPanel, BorderLayout.WEST);
		overviewPanel.setLayout(new BorderLayout(0, 0));
		listModel = new DefaultListModel();
		macroList = new JList(listModel);
		macroList.setBorder(new LineBorder(new Color(0, 0, 0)));
		macroList.setPreferredSize(new Dimension(150, 0));
		overviewPanel.add(macroList, BorderLayout.CENTER);
		JPopupMenu macroMenuPopupMenu = new JPopupMenu();
		addPopup(macroList, macroMenuPopupMenu);
		JMenuItem newMacroMenuItem = new JMenuItem("Neues Makro");
		macroMenuPopupMenu.add(newMacroMenuItem);
		JMenuItem removeMacroMenuItem = new JMenuItem("Makro Löschen");
		macroMenuPopupMenu.add(removeMacroMenuItem);
		JPanel macroPanel = new JPanel();
		contentPane.add(macroPanel, BorderLayout.CENTER);
		macroPanel.setLayout(new BorderLayout(0, 0));
		macroNameTextField = new JTextField();
		macroNameTextField.setEnabled(false);
		macroNameTextField.setBorder(new LineBorder(new Color(171, 173, 179)));
		macroPanel.add(macroNameTextField, BorderLayout.NORTH);
		macroNameTextField.setColumns(10);
		macroContentTextPane = new JTextPane();
		macroContentTextPane.setEnabled(false);
		macroContentTextPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		macroPanel.add(macroContentTextPane, BorderLayout.CENTER);
		// close application menu item
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		// update macro name in list and data when it changes
		macroNameTextField.getDocument().addDocumentListener(
				new DocumentListener() {
					private void inputChanged() {
						if (macroList.getSelectedIndex() != -1) {
							listModel.setElementAt(
									macroNameTextField.getText(),
									macroList.getSelectedIndex());
							macros.get(macroList.getSelectedIndex()).setKey(
									macroNameTextField.getText());
						}
					}

					public void changedUpdate(DocumentEvent arg0) {
						inputChanged();
					}

					public void insertUpdate(DocumentEvent arg0) {
						inputChanged();
					}

					public void removeUpdate(DocumentEvent arg0) {
						inputChanged();
					}
				});
		//update macro content in data
		macroContentTextPane.getDocument().addDocumentListener(new DocumentListener() {
			private void inputChanged() {
				macros.get(macroList.getSelectedIndex()).setValue(
						macroContentTextPane.getText());
			}
			public void changedUpdate(DocumentEvent arg0) {
				inputChanged();
			}
			public void insertUpdate(DocumentEvent arg0) {
				inputChanged();
			}
			public void removeUpdate(DocumentEvent arg0) {
				inputChanged();
			}

		});
		// update which macro-content is showed
		macroList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (macroList.getSelectedIndex() != -1) {
					macroNameTextField.setText(macros.get(
							macroList.getSelectedIndex()).getKey());
					macroContentTextPane.setText(macros.get(
							macroList.getSelectedIndex()).getValue());
					macroNameTextField.setEnabled(true);
					macroContentTextPane.setEnabled(true);
				} else {
					macroNameTextField.setText("");
					macroNameTextField.setEnabled(false);
					macroContentTextPane.setText("");
					macroContentTextPane.setEnabled(false);
				}
			}

		});
		//new macro
		newMacroMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name = JOptionPane.showInputDialog(null,
						"Makronamen eingeben: ", "Neues Makro", 1);
				if (name != null) {
					addMacro(name, "");
				}
			}
		});
		//remove macro
		removeMacroMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selection = macroList.getSelectedIndex();
				if (selection != -1) {
					removeMacro(selection);
				}
			}
		});
		//save macro
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Document xmlTree = null;
				try {
					xmlTree = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().newDocument();
					Element xmlRoot = xmlTree.createElement("macros");
					xmlTree.appendChild(xmlRoot);
					Element xmlChild;
					for (Pair<String, String> child : macros) {
						xmlChild = xmlTree.createElement("macro");
						xmlChild.setAttribute("name", child.getKey());
						xmlChild.setTextContent(child.getValue());
						xmlRoot.appendChild(xmlChild);
					}
				} catch (ParserConfigurationException e1) {
					e1.printStackTrace();
				}
				try {
					if (xmlTree != null) {
						TransformerFactory
								.newInstance()
								.newTransformer()
								.transform(new DOMSource(xmlTree),
										new StreamResult("macro.xml"));
					}
				} catch (TransformerConfigurationException e1) {
					e1.printStackTrace();
				} catch (TransformerException e1) {
					e1.printStackTrace();
				} catch (TransformerFactoryConfigurationError e1) {
					e1.printStackTrace();
				}
			}
		});
		// drag and drop
		MouseInputListener mouseHandler = new MouseInputAdapter() {
			private String macroName, macroContent;
			private int fromIndex;

			public void mousePressed(final MouseEvent evt) {
				macroName = macroNameTextField.getText();
				macroContent = macroContentTextPane.getText();
				fromIndex = macroList.getSelectedIndex();
			}

			public void mouseDragged(final MouseEvent evt) {
				if (macroList.getSelectedIndex() != -1) {
					int toIndex = macroList.locationToIndex(evt.getPoint());
					if (toIndex != fromIndex) {
						removeMacro(fromIndex);
						insertMacro(toIndex, macroName, macroContent);
						fromIndex = toIndex;
					}
				}
			}
		};
		macroList.addMouseListener(mouseHandler);
		macroList.addMouseMotionListener(mouseHandler);
		//load data
		loadMacros();
	}

	/**
	 * load the data from macro.xml
	 */
	private void loadMacros() {
		macros = new ArrayList<Pair<String, String>>();
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse("macro.xml");
			Node xmlRoot = doc.getFirstChild();
			Node child = null;
			NodeList children = xmlRoot.getChildNodes();
			String makroName = "";
			String makroContent = "";
			for (int i = 0; i < children.getLength(); i++) {
				child = children.item(i);
				makroName = child.getAttributes().getNamedItem("name")
						.getNodeValue();
				makroContent = child.getTextContent();
				addMacro(makroName, makroContent);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds a macro
	 * @param macroName name of the macro
	 * @param makroContent content of the macro
	 */
	private void addMacro(String macroName, String makroContent) {
		macros.add(new Pair<String, String>(macroName, makroContent));
		listModel.addElement(macroName);
	}

	/**
	 * removes a macro
	 * @param index index of the macro in the list
	 */
	private void removeMacro(int index) {
		macros.remove(index);
		listModel.remove(index);
	}

	/**
	 * inserts a macro
	 * @param index position for insertion
	 * @param macroName name of the macro
	 * @param macroContent content of the macro
	 */
	private void insertMacro(int index, String macroName, String macroContent) {
		macros.add(index, new Pair<String, String>(macroName, macroContent));
		listModel.add(index, macroName);
	}

	/**
	 * adds a popup menu
	 * @param component who should get the popup menu
	 * @param popup the popup menu which should be added
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

}
