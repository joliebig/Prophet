package experimentGUI.plugins.codeViewerPlugin.fileTree;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;


@SuppressWarnings("serial")
public class FileTreeNode extends DefaultMutableTreeNode {	
	private String name;
	private String path;
	private boolean isFile;
	
	public FileTreeNode(File file) {
		this(file,"", true);
	}
	private FileTreeNode(File file, String containingPath, boolean root) {
		name=file.getName();
		if (root) {
			this.path="";
		} else {
			this.path=containingPath+System.getProperty("file.separator")+name;
		}
		isFile = file.isFile();
		if(!isFile) {
			File[] content = file.listFiles();
			Arrays.sort(content);
			Vector<FileTreeNode> dirs = new Vector<FileTreeNode>();
			Vector<FileTreeNode> files = new Vector<FileTreeNode>();
			for (File f : content) {
				if (f.isDirectory()) {
					dirs.add(new FileTreeNode(f, this.path, false));
				} else {
					files.add(new FileTreeNode(f, this.path, false));
				}
			}
			for (FileTreeNode newDir : dirs) {
				this.add(newDir);
			}
			for (FileTreeNode newFile : files) {
				this.add(newFile);
			}
		}
	}
	public String getFilePath() {
		return path;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public boolean isLeaf() {
		return isFile;
	}
	public boolean isFile() {
		return isFile;
	}
}
