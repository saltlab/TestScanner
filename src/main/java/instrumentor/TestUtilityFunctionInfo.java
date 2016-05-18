package instrumentor;

import java.util.ArrayList;

/**
 * A test utility function is a function written in a test suite that is called in test cases
 * @author aminmf
 *
 */
public class TestUtilityFunctionInfo {
	private String funcName = "";
	private int numFunCall = 0;  // number of unique function calls in the test case
	private int numAssertions = 0;
	private int numObjCreation = 0;
	private int numTriggers = 0;

	public TestUtilityFunctionInfo(String funcName){
		this.funcName = funcName;
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

	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	public int getNumFunCall() {
		return functionCalls.size();
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
