package experimentGUI.util.searchBar;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultTreeModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchEngine;

import experimentGUI.plugins.codeViewerPlugin.CodeViewer;
import experimentGUI.plugins.codeViewerPlugin.fileTree.FileEvent;
import experimentGUI.plugins.codeViewerPlugin.fileTree.FileListener;
import experimentGUI.plugins.codeViewerPlugin.fileTree.FileTree;
import experimentGUI.plugins.codeViewerPlugin.fileTree.FileTreeModel;
import experimentGUI.plugins.codeViewerPlugin.fileTree.FileTreeNode;

/**
 * This class adds a JTextPane to a searchbar which is created. With this
 * searchBar the user can search through the text in the JTextPane User
 *
 * @author Robert Futrell, Markus Köppen, Andreas Hasselberg
 */

public class GlobalSearchBar extends JToolBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final String CAPTION_FIND = "Suche";
	public static final String CAPTION_REGEX = "Regex";
	public static final String CAPTION_MATCH_CASE = "Groß-/Kleinschreibung";
	public static final String ACTION_NEXT = "Global";

	private JLabel searchLabel = new JLabel("Globale Suche");
    private JTextField searchField = new JTextField(30);
	private JButton forwardButton = new JButton(CAPTION_FIND);
	private JCheckBox regexCB = new JCheckBox(CAPTION_REGEX);
	private JCheckBox matchCaseCB = new JCheckBox(CAPTION_MATCH_CASE);

	private File file;
	private FileTree tree;
	private CodeViewer viewer;

	private Vector<SearchBarListener> listeners = new Vector<SearchBarListener>();

	public void addSearchBarListener(SearchBarListener l) {
		listeners.add(l);
	}

	public void removeSearchBarListener(SearchBarListener l) {
		listeners.remove(l);
	}

	/**
	 * Grabs the focus
	 */
	public void grabFocus() {
		searchField.grabFocus();
	}

	public GlobalSearchBar(File file, CodeViewer v) {
		viewer = v;
		this.setFloatable(false);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JPanel northPanel = new JPanel();
		searchField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					forwardButton.doClick();
				}
			}
		});
		northPanel.add(searchLabel);
		northPanel.add(searchField);
		forwardButton.setActionCommand(ACTION_NEXT);
		forwardButton.addActionListener(this);

		tree = new FileTree(null);
		tree.addFileListener(new FileListener() {

			@Override
			public void fileEventOccured(FileEvent e) {
				viewer.getTabbedPane().openFile(e.getFilePath());
			}

		});
		this.file=file;

		northPanel.add(forwardButton);
		northPanel.add(regexCB);
		northPanel.add(matchCaseCB);
		mainPanel.add(northPanel,BorderLayout.NORTH);
		mainPanel.add(tree, BorderLayout.CENTER);
		add(mainPanel);
	}

	public void actionPerformed(ActionEvent action) {
		String command = action.getActionCommand();

		String text = searchField.getText();
		if (text.length() == 0) {
			return;
		}

		FileTreeNode root;
		try {
			root = new FileTreeNode(file);
		} catch (FileNotFoundException e1) {
			tree.getTree().setModel(new DefaultTreeModel(null));
			return;
		}


		if (getNextLeaf(root)==null) {
			root.removeAllChildren();
		} else {
			boolean forward = true;
			boolean matchCase = matchCaseCB.isSelected();
			boolean wholeWord = false;
			boolean regex = regexCB.isSelected();

			FileTreeNode current = getNextLeaf(root);
			FileTreeNode delete = null;

			RSyntaxTextArea textArea = new RSyntaxTextArea();

			while(current!=null) {
				if (current.isFile()) {
					try {
						String path = file.getPath()+current.getFilePath();
						File currentFile = new File(path);
						byte[] buffer = new byte[(int) (currentFile).length()];
					    FileInputStream fileStream = new FileInputStream(currentFile);
					    fileStream.read(buffer);
					    textArea.setText(new String(buffer));
					    textArea.setCaretPosition(0);
					    boolean found = SearchEngine.find(textArea, text, forward, matchCase, wholeWord, regex);

					    if (!found) {
					    	delete=current;
					    }
					} catch (Exception e) {
						delete = current;
					}
				} else {
					delete = current;
				}
				current=getNextLeaf(current);
				while (delete!=null) {
					FileTreeNode parent = (FileTreeNode)delete.getParent();
					delete.removeFromParent();
					if (parent!=null && parent.getChildCount()==0) {
						delete=parent;
					} else {
						delete=null;
					}
				}
			}
		}

		tree.getTree().setModel(new FileTreeModel(root));

		for (SearchBarListener l : listeners) {
			l.searched(command, text, root.getChildCount()>0);
		}
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public JButton getForwardButton() {
		return forwardButton;
	}

	public JCheckBox getRegexCB() {
		return regexCB;
	}

	public JCheckBox getMatchCaseCB() {
		return matchCaseCB;
	}

	public FileTree getTree() {
		return tree;
	}

	private FileTreeNode getNextLeaf(FileTreeNode node) {
		do {
			node=(FileTreeNode)node.getNextNode();
		} while (node!=null && !node.isLeaf());
		return node;
	}
}
