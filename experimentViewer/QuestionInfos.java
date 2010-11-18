package experimentViewer;

import java.util.ArrayList;

import util.StringTuple;
/**
 * this class stores all infos to a question
 * the question itself and the yet given answers
 * 
 * @author Markus K�ppen, Andreas Hasselberg
 *
 */
public class QuestionInfos {
	String key;	//the name of the question
	String value;	//the content of the question
	
	ArrayList<StringTuple> answers;	//all yet given answers
	
	/**
	 * creates an empty question
	 */
	public QuestionInfos() {
		key = "";
		value = "";		
		answers = new ArrayList<StringTuple>();
	}
	
	/**
	 * creates a question with a given content and name
	 * @param key name of the question
	 * @param value content of the question
	 */
	public QuestionInfos(String key, String value) {
		this.key = key;
		this.value = value;			
		answers = new ArrayList<StringTuple>();	
	}
	
	/**
	 * returns the name of the question
	 * @return name of the question
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * returns the content of the question
	 * @return content of the question
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * adds an answer to the question
	 * @param name name of the answer
	 * @param answer content of the answer
	 */
	public void addAnswer(String name, String answer) {
		answers.add(new StringTuple(name, answer));
	}
	
	/**
	 * returns all yet given answers
	 * @return yet given answers
	 */
	public ArrayList<StringTuple> getAnswers() {
		return answers;
	}
}