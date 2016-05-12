package instrumentor;

public class TestCaseInfo {
	private int testNumber;
	private String type;   // {"async", "sync"}
	private int numFunCall = 0;  // number of unique function calls in the test case
	private int numAssertions = 0;
	private int numObjCreation = 0;
	private int numTriggers = 0;

	public TestCaseInfo(int testNumber, String type){
		this.testNumber = testNumber;
		this.type = type;
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
}
