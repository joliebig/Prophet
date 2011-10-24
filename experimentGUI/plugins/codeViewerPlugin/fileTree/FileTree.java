package experimentGUI.plugins.codeViewerPlugin.fileTree;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Display a file system in a JTree view
 *
 * @author Andreas Hasselberg, Markus Koeppen
 */
@SuppressWarnings("serial")
public class FileTree extends JScrollPane {
	private JTree tree;
	private File rootDir;
	private Vector<FileListener> fileListeners;

	private FileTreeNode root;

	public FileTree(File dir) {
		rootDir = dir;
//		FileTreeNode treeNode;
		try {
			root = new FileTreeNode(rootDir);
			tree = new JTree(new FileTreeModel(root));
		} catch (FileNotFoundException e1) {
			tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
		}
		this.setMinimumSize(new Dimension(150,0));
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if ((e.getClickCount() == 2)
						&& (selPath != null)
						&& ((FileTreeNode) selPath.getLastPathComponent())
								.isFile()) {
					fireFileEvent(((FileTreeNode) selPath.getLastPathComponent())
							.getFilePath());
				}
			}
		});
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		setViewportView(tree);
	}

	/*
	 * Vorbereitungen zum Casten eines ActionEvents
	 */
	public void addFileListener(FileListener l) {
		if (fileListeners == null)
			fileListeners = new Vector<FileListener>();
		fileListeners.addElement(l);
	}

	public void removeFileListener(FileListener l) {
		if (fileListeners != null)
			fileListeners.removeElement(l);
	}

	public void fireFileEvent(String filePath) {
		if (fileListeners == null)
			return;
		FileEvent event = new FileEvent(this, FileEvent.FILE_OPENED, filePath);
		for (Enumeration<FileListener> e = fileListeners.elements(); e
				.hasMoreElements();)
			((FileListener) e.nextElement()).fileEventOccured(event);
	}

	public void selectFile(String path) {
		if(path.startsWith(System.getProperty("file.separator"))) {
			path = path.substring(System.getProperty("file.separator").length());
		}
		ArrayList<String> pathElements = new ArrayList<String>();
		if(root!= null) {
			pathElements.add(root.toString());
			int pos = path.indexOf(System.getProperty("file.separator"));
			while(pos != -1) {
				pathElements.add(path.substring(0, pos));
				path = path.substring(pos+1);
				pos = path.indexOf(System.getProperty("file.separator"));
			}
			pathElements.add(path);
			ArrayList<Object> treePathList = new ArrayList<Object>();
			treePathList.add(root);
			FileTreeNode currentNode = root;
			for(int i=0; i<pathElements.size(); i++) {
				for(int j=0; j<currentNode.getChildCount(); j++) {
					if(currentNode.getChildAt(j).toString().equals(pathElements.get(i))) {
						currentNode = (FileTreeNode) currentNode.getChildAt(j);
						treePathList.add(currentNode);
						break;
					}
				}
			}
			TreePath selection = new TreePath(treePathList.toArray());
			tree.expandPath(selection);
			tree.setSelectionPath(selection);
		}
	}
	public JTree getTree() {
		return tree;
	}
}