package experimentGUI.plugins.questionListPlugin;

import java.util.Enumeration;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import experimentGUI.util.questionTreeNode.QuestionTreeNode;

/**
 * Creates a panel which consits JList's. The Lists represents categories and
 * every item in the list represents a question
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 */
public class QuestionList extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private QuestionTreeNode root;
	private JTree tree;

	/**
	 * Constructor which creates an Empty Panel
	 */
	public QuestionList(QuestionTreeNode root) {
		this.root = root;
		tree = new JTree(root);
		tree.setEnabled(false);
		tree.setCellRenderer(new SimpleTreeCellRenderer());
		// expand all
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}

		this.setViewportView(tree);
	}

	// public QuestionList(QuestionTreeNode root) {
	// this.root=root;
	// textPane = new JTextPane();
	// textPane.setEditorKit(new HTMLEditorKit());
	// textPane.setEditable(false);
	// this.setViewportView(textPane);
	// }

	public void visit(QuestionTreeNode selectionNode) {
		Enumeration<?> e = root.breadthFirstEnumeration();
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
			if(node.equals(selectionNode)) {
				TreePath path = new TreePath(node.getPath());
				tree.setSelectionPath(path);
				break;
			}
		}
	}

	// public void visit(QuestionTreeNode node) {
	// String content = "<html><body>";
	// boolean inactive =
	// Boolean.parseBoolean(root.getAttributeValue("inactive"));
	// if (!inactive) {
	// content+=print(root,node);
	// }
	// content+="</body></html>";
	// textPane.setText(content);
	// }

//	private String print(QuestionTreeNode node, QuestionTreeNode selected) {
//		String name = node == selected ? "<b><u>" + node.getName().toUpperCase() + "</u></b>" : node
//				.getName();
//		String subNodes = "";
//		if (node.getChildCount() > 0) {
//			String nodeList = "";
//			for (int i = 0; i < node.getChildCount(); i++) {
//				QuestionTreeNode subNode = (QuestionTreeNode) node.getChildAt(i);
//				boolean inactive = Boolean.parseBoolean(subNode.getAttributeValue("inactive"));
//				if (!inactive) {
//					String nodeText = print(subNode, selected);
//					nodeList += nodeText.length() > 0 ? "<li>" + nodeText + "</li>" : "";
//				}
//			}
//			if (nodeList.length() > 0) {
//				subNodes = "<ul>" + nodeList + "</ul>";
//			}
//		}
//		boolean donotshowcontent = Boolean.parseBoolean(node.getAttributeValue("donotshowcontent"));
//		return donotshowcontent && subNodes.length() == 0 ? "" : name + subNodes;
//	}
}
