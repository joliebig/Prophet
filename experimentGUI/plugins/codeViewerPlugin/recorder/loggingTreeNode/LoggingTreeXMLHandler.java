/**
 * this class consist methods which could write an DataTreeNode to an xml file
 * or read an xml file into a DataTreeNode
 *
 * @author Markus Köppen, Andreas Hasselberg
 */

package experimentGUI.plugins.codeViewerPlugin.recorder.loggingTreeNode;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class LoggingTreeXMLHandler {

	/**
	 * method which adds recursively the childs (to an xml file)
	 *
	 * @param treeChilds
	 *            childs which should bye added
	 * @param xmlParent
	 *            the parent who should get the childs
	 * @param xmlTree
	 *            the xml-document
	 */
	private static void saveXMLNode(Document xmlTree, Element xmlNode, LoggingTreeNode treeNode) {
		// evtl. Attribute hinzuf�gen
		if (treeNode.getAttributes().size()>0) {
			for (Entry<String, String> attribute : treeNode.getAttributes().entrySet()) {
				xmlNode.setAttribute(attribute.getKey(), attribute.getValue());
			}
		}
		for (int i=0; i<treeNode.getChildCount(); i++) {
			LoggingTreeNode treeChild = (LoggingTreeNode)treeNode.getChildAt(i);
			Element xmlChild = xmlTree.createElement(treeChild.getType());
			xmlNode.appendChild(xmlChild);
			saveXMLNode(xmlTree, xmlChild, treeChild);
		}
	}

	/**
	 * writes an DataTreeNode with his children into an XML-File
	 *
	 * @param treeRoot
	 *            DataTreeNode which should be added (with children)
	 * @param path
	 *            path for the xml-file
	 */
	public static void saveXMLTree(LoggingTreeNode treeRoot, String path) {
		Document xmlTree = null;
		try {
			// Dokument erstellen
			File dir = new File(path).getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			xmlTree = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			// Wurzelknoten erschaffen
			Element xmlRoot = xmlTree.createElement(treeRoot.getType());
			xmlTree.appendChild(xmlRoot);
			saveXMLNode(xmlTree, xmlRoot, treeRoot);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		// in Datei speichern
		try {
			if (xmlTree != null) {
				TransformerFactory
						.newInstance()
						.newTransformer()
						.transform(new DOMSource(xmlTree),
								new StreamResult(path));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} catch (Error e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * method which adds recursively the childs (to a DataTreeNode file)
	 *
	 * @param xmlChildren
	 *            the childs which should be added
	 * @param treeParent
	 *            the DataTreeNode which should get the childs
	 */
	private static LoggingTreeNode loadXMLNode(Node xmlNode) {
		LoggingTreeNode result = new LoggingTreeNode(xmlNode.getNodeName());
		NamedNodeMap attributes = xmlNode.getAttributes();
		//Attribute hinzuf�gen
		for (int i=0; i<attributes.getLength();i++) {
			Node attribute = attributes.item(i);
			result.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
		}
		//Kinder hinzuf�gen
		NodeList xmlChildrenList = xmlNode.getChildNodes();
		for (int i=0; i<xmlChildrenList.getLength();i++) {
			Node xmlChildNode = xmlChildrenList.item(i);
			result.add(loadXMLNode(xmlChildNode));
		}
		return result;
	}

	/**
	 * loads an xml file an creates an corresponding DataTreeNode
	 *
	 * @param path
	 *            path of the file
	 * @return root of the new tree-structure
	 */
	public static LoggingTreeNode loadXMLTree(String path) {
		try {
			// Document lesen
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(new File(path));
			// Wurzel holen
			Node xmlRoot = doc.getFirstChild();
			return loadXMLNode(xmlRoot);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
