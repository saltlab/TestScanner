package instrumentor;

import java.util.ArrayList;

public class TestCaseInfo {
	private int testNumber;
	private String type;   // {"async", "sync"}
	private int numFunCall = 0;  // number of unique function calls in the test case
	private int numAssertions = 0;
	private int numObjCreation = 0;
	private int numTriggers = 0;
	private int beginLineNum = 0;
	private int endLineNum = 0;

	public TestCaseInfo(int testNumber, String type){
		this.testNumber = testNumber;
		this.type = type;
	}

	private ArrayList<String> functionCalls = new ArrayList<String>();
	public ArrayList<String> getFunctionCalls() {
		return functionCalls;
	}
	public void addFunctionCall(String fc) {
		if (!functionCalls.contains(fc))
			functionCalls.add(fc);
		else
			System.out.println("Repeated!");
	}

	
	public void setBeginEndLines(int begin, int end){
		this.beginLineNum = begin;
		this.endLineNum = end;
	}
	
	public boolean containsLine(int lineNum){
		if (lineNum >= beginLineNum && lineNum <= endLineNum)
			return true;
		System.out.println("Line " + lineNum + " is out of test function [" + beginLineNum + "-" + endLineNum + "]");
		return false;
	}

	
	public int getTestNumber() {
		return testNumber;
	}
	public void setTestNumber(int testNumber) {
		this.testNumber = testNumber;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getNumFunCall() {
		return numFunCall;
	}
	public void setNumFunCall(int numFunCall) {
		this.numFunCall = numFunCall;
	}
	public int getNumAssertions() {
		return numAssertions;
	}
	public void setNumAssertions(int numAssertions) {
		this.numAssertions = numAssertions;
	}
	public int getNumObjCreation() {
		return numObjCreation;
	}
	public void setNumObjCreation(int numObjCreation) {
		this.numObjCreation = numObjCreation;
	}
	public int getNumTriggers() {
		return numTriggers;
	}
	public void setNumTriggers(int numTriggers) {
		this.numTriggers = numTriggers;
	}
	
	public void addModuleStat(TestModuleInfo tmi) {
		this.numFunCall += tmi.getNumFunCall();
		this.numAssertions += tmi.getNumAssertions();
		this.numObjCreation += tmi.getNumObjCreation();
		this.numTriggers += tmi.getNumTriggers();
	}
	
	
}
