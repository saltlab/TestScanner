package instrumentor;

import java.util.ArrayList;

public class TestModuleInfo {
	private int moduleNumber;
	private int numFunCall = 0;  // number of unique function calls in the test case
	private int numAssertions = 0;
	private int numObjCreation = 0;
	private int numTriggers = 0;
	private int beginLineNum = 0;
	private int endLineNum = 0;

	public TestModuleInfo(int testNumber){
		this.moduleNumber = testNumber;
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
		return moduleNumber;
	}
	public void setTestNumber(int testNumber) {
		this.moduleNumber = testNumber;
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
}
