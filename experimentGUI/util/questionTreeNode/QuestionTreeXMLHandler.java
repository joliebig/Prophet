/**
 * this class consist methods which could write an DataTreeNode to an xml file
 * or read an xml file into a DataTreeNode
 *
 * @author Markus Köppen, Andreas Hasselberg
 */

package experimentGUI.util.questionTreeNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
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

import experimentGUI.Constants;
import experimentGUI.util.Pair;

public class QuestionTreeXMLHandler {
	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_VALUE = "value";
	public final static String ATTRIBUTE_TIME = "time";
	public final static String TYPE_ATTRIBUTES = "attributes";
	public final static String TYPE_ANSWERS = "answers";
	public final static String TYPE_ANSWER = "answer";
	public final static String TYPE_CHILDREN = "children";

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
	private static void saveXMLNode(Document xmlTree, Element xmlNode, QuestionTreeNode treeNode) {
		// Name und Value hinzuf�gen
		xmlNode.setAttribute(ATTRIBUTE_NAME, treeNode.getName());
		xmlNode.setAttribute(ATTRIBUTE_VALUE, treeNode.getValue());
		// evtl. Attribute hinzuf�gen
		if (treeNode.getAttributes().size() > 0) {
			Element xmlAttributesNode = xmlTree.createElement(TYPE_ATTRIBUTES);
			xmlNode.appendChild(xmlAttributesNode);
			for (Entry<String, QuestionTreeNode> attributeNode : treeNode.getAttributes().entrySet()) {
				QuestionTreeNode treeChild = attributeNode.getValue();
				Element xmlChild = xmlTree.createElement(treeChild.getType());
				xmlAttributesNode.appendChild(xmlChild);
				saveXMLNode(xmlTree, xmlChild, treeChild);
			}
		}
		// evtl. Kinder hinzuf�gen
		if (treeNode.getChildCount() > 0) {
			Element xmlChildrenNode = xmlTree.createElement(TYPE_CHILDREN);
			xmlNode.appendChild(xmlChildrenNode);
			for (int i = 0; i < treeNode.getChildCount(); i++) {
				QuestionTreeNode treeChild = (QuestionTreeNode) treeNode.getChildAt(i);
				Element xmlChild = xmlTree.createElement(treeChild.getType());
				xmlChildrenNode.appendChild(xmlChild);
				saveXMLNode(xmlTree, xmlChild, treeChild);
			}
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
	public static void saveXMLTree(QuestionTreeNode treeRoot, String path) {
		Document xmlTree = null;
		try {
			// Dokument erstellen
			File dir = new File(path).getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			xmlTree = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			// Wurzelknoten erschaffen
			Element xmlRoot = xmlTree.createElement(treeRoot.getType());
			xmlTree.appendChild(xmlRoot);
			saveXMLNode(xmlTree, xmlRoot, treeRoot);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		// Fragebogen in Datei speichern
		try {
			if (xmlTree != null) {
				TransformerFactory.newInstance().newTransformer()
						.transform(new DOMSource(xmlTree), new StreamResult(path));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} catch (Error e1) {
			e1.printStackTrace();
		}
	}

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
	private static void saveXMLAnswerNode(Document xmlTree, Element xmlNode, QuestionTreeNode treeNode) {
		// Name hinzuf�gen
		xmlNode.setAttribute(ATTRIBUTE_NAME, treeNode.getName());
		xmlNode.setAttribute(ATTRIBUTE_TIME, "" + treeNode.getAnswerTime());
		// evtl. Antworten hinzuf�gen
		if (treeNode.getAnswers().size() > 0) {
			Element xmlAnswersNode = xmlTree.createElement(TYPE_ANSWERS);
			xmlNode.appendChild(xmlAnswersNode);
			for (Entry<String, String> answerEntry : treeNode.getAnswers().entrySet()) {
				Element xmlChild = xmlTree.createElement(TYPE_ANSWER);
				xmlChild.setAttribute(ATTRIBUTE_NAME, answerEntry.getKey());
				xmlChild.setAttribute(ATTRIBUTE_VALUE, answerEntry.getValue());
				xmlAnswersNode.appendChild(xmlChild);
			}
		}
		// evtl. Kinder hinzuf�gen
		if (treeNode.getChildCount() > 0) {
			Element xmlChildrenNode = xmlTree.createElement(TYPE_CHILDREN);
			xmlNode.appendChild(xmlChildrenNode);
			for (int i = 0; i < treeNode.getChildCount(); i++) {
				QuestionTreeNode treeChild = (QuestionTreeNode) treeNode.getChildAt(i);
				Element xmlChild = xmlTree.createElement(treeChild.getType());
				xmlChildrenNode.appendChild(xmlChild);
				saveXMLAnswerNode(xmlTree, xmlChild, treeChild);
			}
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
	public static void saveXMLAnswerTree(QuestionTreeNode treeRoot, String path) {
		Document xmlTree = null;
		try {
			// Dokument erstellen
			File dir = new File(path).getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			xmlTree = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			// Wurzelknoten erschaffen
			Element xmlRoot = xmlTree.createElement(treeRoot.getType());
			xmlTree.appendChild(xmlRoot);
			saveXMLAnswerNode(xmlTree, xmlRoot, treeRoot);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		// Fragebogen in Datei speichern
		try {
			if (xmlTree != null) {
				TransformerFactory.newInstance().newTransformer()
						.transform(new DOMSource(xmlTree), new StreamResult(path));
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
	private static QuestionTreeNode loadXMLNode(Node xmlNode) {
		QuestionTreeNode result = new QuestionTreeNode(xmlNode.getNodeName());
		// Name und Value setzen
		Node xmlNameNode = xmlNode.getAttributes().getNamedItem(ATTRIBUTE_NAME);
		if (xmlNameNode != null) {
			result.setName(xmlNameNode.getNodeValue());
		}
		Node xmlValueNode = xmlNode.getAttributes().getNamedItem(ATTRIBUTE_VALUE);
		if (xmlValueNode != null) {
			result.setValue(xmlValueNode.getNodeValue());
		}
		// Attribute und Kinder hinzuf�gen
		NodeList xmlMetaNodes = xmlNode.getChildNodes();
		for (int i = 0; i < xmlMetaNodes.getLength(); i++) {
			Node xmlMetaNode = xmlMetaNodes.item(i);
			if (xmlMetaNode.getNodeName().equals(TYPE_ATTRIBUTES) && xmlMetaNode.hasChildNodes()) {
				NodeList xmlAttributesList = xmlMetaNode.getChildNodes();
				for (int j = 0; j < xmlAttributesList.getLength(); j++) {
					Node xmlAttributeNode = xmlAttributesList.item(j);
					QuestionTreeNode attributeNode = loadXMLNode(xmlAttributeNode);
					result.setAttribute(attributeNode.getName(), attributeNode);
				}
			} else if (xmlMetaNode.getNodeName().equals(TYPE_CHILDREN) && xmlMetaNode.hasChildNodes()) {
				NodeList xmlChildrenList = xmlMetaNode.getChildNodes();
				for (int j = 0; j < xmlChildrenList.getLength(); j++) {
					Node xmlChildNode = xmlChildrenList.item(j);
					result.add(loadXMLNode(xmlChildNode));
				}
			}
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
	public static QuestionTreeNode loadXMLTree(String path) throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		try {
			// Document lesen
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			// Wurzel holen
			Node xmlRoot = doc.getFirstChild();
			return loadXMLNode(xmlRoot);
		} catch (Exception e) {
			return null;
		}
	}

	private static QuestionTreeNode loadAnswerXMLNode(Node xmlNode) {
		QuestionTreeNode result = new QuestionTreeNode(xmlNode.getNodeName());
		Node xmlNameNode = xmlNode.getAttributes().getNamedItem(ATTRIBUTE_NAME);
		// Name und Value setzen
		if (xmlNameNode != null) {
			result.setName(xmlNameNode.getNodeValue());
		}
		Node xmlValueNode = xmlNode.getAttributes().getNamedItem(ATTRIBUTE_VALUE);
		if (xmlValueNode != null) {
			result.setValue(xmlValueNode.getNodeValue());
		}
		// Attribute hinzuf�gen
		NamedNodeMap xmlAttributeMap = xmlNode.getAttributes();
		for (int i = 0; i < xmlAttributeMap.getLength(); i++) {
			Node xmlAttribute = xmlAttributeMap.item(i);
			QuestionTreeNode attributeNode = new QuestionTreeNode(QuestionTreeNode.TYPE_ATTRIBUTE,
					xmlAttribute.getNodeName());
			attributeNode.setValue(xmlAttribute.getNodeValue());
			result.setAttribute(xmlAttribute.getNodeName(), attributeNode);
		}
		// Answers und Kinder hinzuf�gen
		NodeList xmlMetaNodes = xmlNode.getChildNodes();
		for (int i = 0; i < xmlMetaNodes.getLength(); i++) {
			Node xmlMetaNode = xmlMetaNodes.item(i);
			if (xmlMetaNode.getNodeName().equals(TYPE_ANSWERS) && xmlMetaNode.hasChildNodes()) {
				NodeList xmlAttributesList = xmlMetaNode.getChildNodes();
				for (int j = 0; j < xmlAttributesList.getLength(); j++) {
					Node xmlAttributeNode = xmlAttributesList.item(j);
					result.setAnswer(xmlAttributeNode.getAttributes().getNamedItem(ATTRIBUTE_NAME)
							.getNodeValue(), xmlAttributeNode.getAttributes().getNamedItem(ATTRIBUTE_VALUE)
							.getNodeValue());
				}
			} else if (xmlMetaNode.getNodeName().equals(TYPE_CHILDREN) && xmlMetaNode.hasChildNodes()) {
				NodeList xmlChildrenList = xmlMetaNode.getChildNodes();
				for (int j = 0; j < xmlChildrenList.getLength(); j++) {
					Node xmlChildNode = xmlChildrenList.item(j);
					result.add(loadAnswerXMLNode(xmlChildNode));
				}
			}
		}
		return result;
	}

	public static QuestionTreeNode loadAnswerXMLTree(String path) throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		try {
			// Document lesen
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			// Wurzel holen
			Node xmlRoot = doc.getFirstChild();
			return loadAnswerXMLNode(xmlRoot);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 *
	 * @param formInfos
	 *            Liste(Knotenname, Liste(Formname, Formvalue)) --> enth�lt alle
	 *            formularknoten
	 * @param answerNodes
	 *            Alle root Knoten aus den answer-xml Dateien (1 pro datei)
	 * @param experimentCode
	 *            code des derzeitigen Experimentes
	 */
	public static void saveAsCSVFile(
			ArrayList<Pair<QuestionTreeNode, ArrayList<Pair<String, String>>>> formInfos,
			ArrayList<QuestionTreeNode> answerNodes, String experimentCode, String path) {
		FileWriter fw;
		BufferedWriter bw;
		String line;
		try {
			fw = new FileWriter(new File(path + System.getProperty("file.separator") +  experimentCode + ".csv"));
			bw = new BufferedWriter(fw);
			// header schreiben
			line = "\"expCode\";\"probCode\"";
			for (int i = 0; i < formInfos.size(); i++) {
				Pair<QuestionTreeNode, ArrayList<Pair<String, String>>> questionInfos = formInfos.get(i);
				line += ";\"" + questionInfos.getKey().getName() + "::time\"";
				ArrayList<Pair<String, String>> questionForms = questionInfos.getValue();
				for (int j = 0; j < questionForms.size(); j++) {
					String formName = questionForms.get(j).getKey();
					if (formName != null) {
						line += ";\"" + questionInfos.getKey().getName() + "::" + formName + "\"";
					}
				}
			}
			bw.write(line + System.getProperty("line.separator"));
			// Daten schreiben
			for (int i = 0; i < answerNodes.size(); i++) {
				int nodeIndex = 0;
				QuestionTreeNode currentNode = answerNodes.get(i);
				line = "\"" + experimentCode.replaceAll("\"", "\"\"") + "\""; //expCode
				//probCode
				if (currentNode.getAnswer(Constants.KEY_SUBJECT) != null) {
					line += ";\"" + currentNode.getAnswer(Constants.KEY_SUBJECT).replaceAll("\"", "\"\"")
							+ "\"";
				} else {
					line += ";\"\"";
				}

				while (currentNode != null) {
					line += ";\"" + currentNode.getAttributeValue(ATTRIBUTE_TIME) + "\""; // Knotenzeit
					// Knotenantworten
					ArrayList<Pair<String, String>> questionForms = formInfos.get(nodeIndex).getValue();
					for (int j = 0; j < questionForms.size(); j++) {
						String value = currentNode.getAnswer(questionForms.get(j).getKey());
						if (value == null) {
							line += ";\"\"";
						} else {
							line += ";\"" + value.replaceAll("\"", "\"\"") + "\"";
						}
					}
					currentNode = (QuestionTreeNode) currentNode.getNextNode();
					nodeIndex++;
				}
				bw.write(line + System.getProperty("line.separator"));
			}
			bw.close();
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Fehler beim Speichern der CSV-Datei.");
		}
	}
}
