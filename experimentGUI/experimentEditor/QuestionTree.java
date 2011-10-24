package experimentGUI.experimentEditor;

/**
 * This Class inherits from JTree.
 * It interacts with EditorData and build there a DataTree (from DataTreeNode) like the data in JTree.
 * It permit limited Drag and Drop, such as Renaming and a 3 Level System.
 * The first Level is the root, the child of the root could have childs again, no more Levels are allowed.
 * All Childs of the second Level get a SettingDialog and EditorData will save the informations.
 * All nodes will have a unique name.
 *
 * @author Markus Köppen, Andreas Hasselberg
 */

import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.questionTreeNode.QuestionTreeNodeEvent;
import experimentGUI.util.questionTreeNode.QuestionTreeNodeListener;

/**
 * JTree within a JScrollPane component that displays the content of a tree
 * based on the QuestionTreeNode class.
 *
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
public class QuestionTree extends JScrollPane {
	private static final long serialVersionUID = 1L;

	/**
	 * the actual JTree component
	 */
	private JTree tree;

	/**
	 * currently selected QuestionTreeNode
	 */
	private QuestionTreeNode selected;

	/**
	 * QuestionTreeNode currently in the Clipboard
	 */
	private QuestionTreeNode clipboard;

	/**
	 * popup menu shown when the experiment node is right clicked
	 */
	private JPopupMenu experimentPopup;

	/**
	 * popup menu shown when a category node is right clicked
	 */
	private JPopupMenu categoryPopup;

	/**
	 * popup menu shown when a question node is right clicked
	 */
	private JPopupMenu questionPopup;
	private JMenuItem experimentPasteMenuItem;
	private JMenuItem categoryPasteMenuItem;

	/**
	 * Collection of listeners that listen on node selections.
	 */
	Vector<QuestionTreeNodeListener> questionTreeListeners;

	/**
	 * String constants for the popup menus
	 */
	public final static String POPUP_NEW_CATEGORY = "Neue Kategorie";
	public final static String POPUP_NEW_QUESTION = "Neue Frage";
	public final static String POPUP_RENAME = "Umbenennen";
	public final static String POPUP_REMOVE = "Löschen";
	public final static String POPUP_COPY = "Kopieren";
	public final static String POPUP_PASTE = "Einfügen";

	/**
	 * String constants for messages
	 */
	public final static String MESSAGE_NAME = "Name:";
	public final static String MESSAGE_NEW_NAME = "Neuer Name:";

	/**
	 * String constants for actions
	 */
	public final static String ACTION_NEW_CATEGORY = "newcategory";
	public final static String ACTION_NEW_QUESTION = "newquestion";
	public final static String ACTION_RENAME = "rename";
	public final static String ACTION_REMOVE = "remove";
	public final static String ACTION_COPY = "copy";
	public final static String ACTION_PASTE = "paste";

	/**
	 * constant which selects the standard name of the root-node in the tree
	 */
	public final static String DEFAULT_EXPERIMENT_NODE_NAME = "Experiment";

	/**
	 * The Constructor of the class.<br>
	 * It creates the popup menus and initializes some settings
	 */
	public QuestionTree() {
		tree = new JTree((TreeModel) null);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setViewportView(tree);
		tree.addMouseListener(new MouseAdapter() {

			private void maybeShowPopup(MouseEvent e) {
				if (tree.getModel() != null && e.isPopupTrigger()) {
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					if (selPath != null) {
						tree.setSelectionPath(selPath);
						selected = (QuestionTreeNode) selPath.getLastPathComponent();
						if (selected.isCategory()) {
							categoryPopup.show(tree, e.getX(), e.getY());
						} else if (selected.isQuestion()) {
							questionPopup.show(tree, e.getX(), e.getY());
						} else if (selected.isExperiment()) {
							experimentPopup.show(tree, e.getX(), e.getY());
						}
					}
				}
			}

			// jl:
			// note mousereleased and mouse pressed have to be checked to make
			// popup appear under linux and windows as well
			// http://stackoverflow.com/questions/5736872/java-popup-trigger-in-linux
			// http://download.oracle.com/javase/tutorial/uiswing/examples/components/PopupMenuDemoProject/src/components/PopupMenuDemo.java
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				TreePath selPath = tse.getPath();
				if (selPath != null) {
					selected = (QuestionTreeNode) selPath.getLastPathComponent();
					fireEvent(selected);
				}
			}
		});

		ActionListener myActionlistener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String name;
				// remove
				if (ae.getActionCommand().equals(ACTION_REMOVE)) {
					removeNode(selected);
				} else
				// newcategory
				if (ae.getActionCommand().equals(ACTION_NEW_CATEGORY)) {
					if ((name = JOptionPane.showInputDialog(MESSAGE_NAME, "")) != null) {
						addNode(selected, QuestionTreeNode.TYPE_CATEGORY, name);
					}
				} else
				// newquestion
				if (ae.getActionCommand().equals(ACTION_NEW_QUESTION)) {
					if ((name = JOptionPane.showInputDialog(MESSAGE_NAME, "")) != null) {
						addNode(selected, QuestionTreeNode.TYPE_QUESTION, name);
					}
				} else
				// rename
				if (ae.getActionCommand().equals(ACTION_RENAME)) {
					if ((name = JOptionPane.showInputDialog(MESSAGE_NEW_NAME, selected.getName())) != null) {
						renameNode(selected, name);
					}
				}
				// copy
				if (ae.getActionCommand().equals(ACTION_COPY)) {
					clipboard = (QuestionTreeNode) selected.copy();

					enablePopupItems();
				}
				// insert
				if (ae.getActionCommand().equals(ACTION_PASTE)) {
//					if (clipboard == null) {
//						JOptionPane.showMessageDialog(null, "Kein Knoten in der Zwischenablage.");
//					} else {
//						if (selected.getType().equals(QuestionTreeNode.TYPE_EXPERIMENT)
//								&& !clipboard.getType().equals(QuestionTreeNode.TYPE_CATEGORY)) {
//							JOptionPane.showMessageDialog(null,
//									"Dem Experimentknoten k�nnen nur Kategorien hinzugef�gt werden.");
//						} else if (selected.getType().equals(QuestionTreeNode.TYPE_CATEGORY)
//								&& !clipboard.getType().equals(QuestionTreeNode.TYPE_QUESTION)) {
//							JOptionPane.showMessageDialog(null,
//									"Einer Kategorie k�nnen nur Fragen hinzugef�gt werden.");
//						} else {
							addNode(selected, (QuestionTreeNode) clipboard.copy());
//						}
//					}
				}
			}
		};

		experimentPopup = new JPopupMenu();
		JMenuItem newCategoryMenuItem = new JMenuItem(POPUP_NEW_CATEGORY);
		newCategoryMenuItem.addActionListener(myActionlistener);
		newCategoryMenuItem.setActionCommand(ACTION_NEW_CATEGORY);
		experimentPopup.add(newCategoryMenuItem);
		experimentPopup.addSeparator();
		JMenuItem renameMenuItem = new JMenuItem(POPUP_RENAME);
		renameMenuItem.addActionListener(myActionlistener);
		renameMenuItem.setActionCommand(ACTION_RENAME);
		experimentPopup.add(renameMenuItem);
		experimentPopup.addSeparator();
		experimentPasteMenuItem = new JMenuItem(POPUP_PASTE);
		experimentPasteMenuItem.addActionListener(myActionlistener);
		experimentPasteMenuItem.setActionCommand(ACTION_PASTE);
		experimentPopup.add(experimentPasteMenuItem);

		categoryPopup = new JPopupMenu();
		JMenuItem newQuestionMenuItem = new JMenuItem(POPUP_NEW_QUESTION);
		newQuestionMenuItem.addActionListener(myActionlistener);
		newQuestionMenuItem.setActionCommand(ACTION_NEW_QUESTION);
		categoryPopup.add(newQuestionMenuItem);
		categoryPopup.addSeparator();
		renameMenuItem = new JMenuItem(POPUP_RENAME);
		renameMenuItem.addActionListener(myActionlistener);
		renameMenuItem.setActionCommand(ACTION_RENAME);
		categoryPopup.add(renameMenuItem);
		JMenuItem removeMenuItem = new JMenuItem(POPUP_REMOVE);
		removeMenuItem.addActionListener(myActionlistener);
		removeMenuItem.setActionCommand(ACTION_REMOVE);
		categoryPopup.add(removeMenuItem);
		categoryPopup.addSeparator();
		JMenuItem copyMenuItem = new JMenuItem(POPUP_COPY);
		copyMenuItem.addActionListener(myActionlistener);
		copyMenuItem.setActionCommand(ACTION_COPY);
		categoryPopup.add(copyMenuItem);
		categoryPasteMenuItem = new JMenuItem(POPUP_PASTE);
		categoryPasteMenuItem.addActionListener(myActionlistener);
		categoryPasteMenuItem.setActionCommand(ACTION_PASTE);
		categoryPopup.add(categoryPasteMenuItem);

		questionPopup = new JPopupMenu();
		renameMenuItem = new JMenuItem(POPUP_RENAME);
		renameMenuItem.addActionListener(myActionlistener);
		renameMenuItem.setActionCommand(ACTION_RENAME);
		questionPopup.add(renameMenuItem);
		removeMenuItem = new JMenuItem(POPUP_REMOVE);
		removeMenuItem.addActionListener(myActionlistener);
		removeMenuItem.setActionCommand(ACTION_REMOVE);
		questionPopup.add(removeMenuItem);
		questionPopup.addSeparator();
		copyMenuItem = new JMenuItem(POPUP_COPY);
		copyMenuItem.addActionListener(myActionlistener);
		copyMenuItem.setActionCommand(ACTION_COPY);
		questionPopup.add(copyMenuItem);

		tree.setDragEnabled(true);
		tree.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;

			public void dragOver(DropTargetDragEvent dtde) {
				QuestionTreeNode source = (QuestionTreeNode) tree.getSelectionPath().getLastPathComponent();
				Point p = dtde.getLocation();
				TreePath selPath = tree.getPathForLocation(p.x, p.y);
				if (selPath == null) {
					dtde.rejectDrag();
					return;
				}
				QuestionTreeNode target = (QuestionTreeNode) selPath.getLastPathComponent();
				// only accept some actions
				// move from level to same level (not important which parent)
				if (!(source.isCategory() && target.isQuestion())) {
					dtde.acceptDrag(dtde.getDropAction());
				} else {
					dtde.rejectDrag();
				}
			}

			public void drop(DropTargetDropEvent dtde) {
				QuestionTreeNode source = (QuestionTreeNode) tree.getSelectionPath().getLastPathComponent();

				if (source.isExperiment()) {
					return;
				}

				Point p = dtde.getLocation();
				TreePath selPath = tree.getPathForLocation(p.x, p.y);
				if (selPath == null) {
					return;
				}
				QuestionTreeNode target = (QuestionTreeNode) selPath.getLastPathComponent();
				QuestionTreeNode parent = (QuestionTreeNode) target.getParent();

				if (source.isCategory() == target.isCategory()) {
					source.removeFromParent();
					parent.insert(source, parent.getIndex(target) + 1);
				} else {
					source.removeFromParent();
					target.insert(source, target.getChildCount());
				}
				tree.updateUI();
			}

			public void dropActionChanged(DropTargetDragEvent arg0) {
			}

			public void dragExit(DropTargetEvent dte) {
			}

			public void dragEnter(DropTargetDragEvent arg0) {
			}
		});

		tree.setEditable(true);

		enablePopupItems();
	}

	/**
	 * (de)activate the popup menu items matching to the current state and selected node
	 */
	private void enablePopupItems() {
		if(clipboard == null) {
			experimentPasteMenuItem.setEnabled(false);
			categoryPasteMenuItem.setEnabled(false);
		} else {
			if(clipboard.getType().equals(QuestionTreeNode.TYPE_CATEGORY)) {
				categoryPasteMenuItem.setEnabled(false);
				experimentPasteMenuItem.setEnabled(true);
			} else if (clipboard.getType().equals(QuestionTreeNode.TYPE_QUESTION)){
				experimentPasteMenuItem.setEnabled(false);
				categoryPasteMenuItem.setEnabled(true);
			}
		}
	}



	/**
	 * renames a node
	 *
	 * @param node
	 *  the node to be renamed
	 * @param name
	 *  a proposal for the name of the node
	 * @return
	 *  boolean value stating if the renaming was successful
	 */
	private boolean renameNode(QuestionTreeNode node, String name) {
		boolean result = node.setName(name);
		tree.updateUI();
		return result;
	}

	/**
	 * remove a node from the tree
	 * @param node
	 *  node to be removed
	 */
	private void removeNode(QuestionTreeNode node) {
		node.removeFromParent();
		tree.updateUI();
	}

	/**
	 * Adds a new node to the tree
	 * @param node
	 *  node the new node will be added to
	 * @param type
	 *  type of the node
	 * @param name
	 *  name of the new node
	 */
	private void addNode(QuestionTreeNode node, String type, String name) {
		QuestionTreeNode newNode = new QuestionTreeNode(type, name);
		node.insert(newNode, node.getChildCount());
		tree.updateUI();
	}

	/**
	 * Adds an existing node to the tree
	 * @param node
	 *  node the new node will be added to
	 * @param insertion
	 *   the node to be inserted
	 */
	private void addNode(QuestionTreeNode node, QuestionTreeNode insertion) {
		node.insert(insertion, node.getChildCount());
		tree.updateUI();
	}

	/**
	 * resets the tree to an empty one
	 */
	public void newRoot() {
		setRoot(new QuestionTreeNode(QuestionTreeNode.TYPE_EXPERIMENT, DEFAULT_EXPERIMENT_NODE_NAME));
	}

	/**
	 * sets the root of the tree (replaces the tree)
	 * @param n
	 *  new experiment tree
	 */
	public void setRoot(QuestionTreeNode n) {
		selected = null;
		if (n != null) {
			tree.setModel(new DefaultTreeModel(n));
			tree.setEnabled(true);
		} else {
			tree.setModel(new DefaultTreeModel(/* new QuestionTreeNode("") */null));
			tree.setEnabled(false);
		}
		tree.updateUI();
		fireEvent(null);
	}

	/**
	 *
	 * @return
	 *  current experiment tree root node
	 */
	public QuestionTreeNode getRoot() {
		return (QuestionTreeNode) tree.getModel().getRoot();
	}

	/**
	 * Adds a TreeNodeListener to the object
	 * @param l
	 *  the listener to be added
	 */
	public void addQuestionTreeNodeListener(QuestionTreeNodeListener l) {
		if (questionTreeListeners == null)
			questionTreeListeners = new Vector<QuestionTreeNodeListener>();
		questionTreeListeners.addElement(l);
	}

	/**
	 * Removes a TreeNodeListener from the object
	 * @param l
	 *  the listener to be removed
	 */
	public void removeQuestionTreeNodeListener(QuestionTreeNodeListener l) {
		if (questionTreeListeners != null)
			questionTreeListeners.removeElement(l);
	}

	/**
	 * notifies all listeners of a QuestionTreeEvent (selecting a node)
	 * @param questionTreeNode
	 *  the selected node
	 */
	private void fireEvent(QuestionTreeNode questionTreeNode) {
		if (questionTreeListeners == null)
			return;
		QuestionTreeNodeEvent event = new QuestionTreeNodeEvent(this, questionTreeNode);
		for (Enumeration<QuestionTreeNodeListener> e = questionTreeListeners.elements(); e.hasMoreElements();)
			((QuestionTreeNodeListener) e.nextElement()).questionTreeEventOccured(event);
	}
}
