package experimentViewer;

import java.util.ArrayList;

import util.StringTuple;

/**
 * This class creates an ArrayList which could get a list of attributes
 * 
 * @author Markus K�ppen, Andreas Hasselberg
 * 
 * @param <T>
 *            Generic type of the ArrayList
 */
public class AttributeArrayList<T> extends ArrayList<T> {

	// Array List which contains the attribute names an attribute values as
	// Strings
	ArrayList<StringTuple> attributes;

	/**
	 * Creates a new AttributeArrayList
	 */
	public AttributeArrayList() {
		super();
		attributes = new ArrayList<StringTuple>();
	}

	/**
	 * Adds an attribute to the ArrayList
	 * 
	 * @param st
	 */
	public void addAttribute(StringTuple st) {
		attributes.add(st);
	}

	/**
	 * Returns an attribute of the ArrayList by index
	 * 
	 * @param index
	 *            index of the attribute
	 * @return StringTupel with the name and the value of the attribute
	 */
	public StringTuple getAttribute(int index) {
		return attributes.get(index);
	}

	/**
	 * Returns the attribute-value to the corresponding attribute name
	 * 
	 * @param name
	 *            name of the attribute
	 * @return value of the attribute, null if not found
	 */
	public String getAttribute(String name) {
		for (StringTuple attribute : attributes) {
			if (attribute.getKey().equals(name)) {
				return attribute.getValue();
			}
		}
		return null;
	}

	/**
	 * Returns an ArrayList with StringTupels which represents the names and
	 * values of the attributes
	 * 
	 * @return list with all attributes
	 */
	public ArrayList<StringTuple> getAttributes() {
		return attributes;
	}
}