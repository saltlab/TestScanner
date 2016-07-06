package instrumentor;

import java.util.ArrayList;

public class FunctionInfo {
	private String name = "";
	private ArrayList<String> params = new ArrayList<String>();				
	private int beginLineNum = 0;
	private int endLineNum = 0;

	public FunctionInfo(String functionName, int beginLineNum, int endLineNum){
		this.name = functionName;
		this.beginLineNum = beginLineNum;
		this.endLineNum = endLineNum;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getParams() {
		return params;
	}

	public int getBeginLineNum() {
		return beginLineNum;
	}

	public int getEndLineNum() {
		return endLineNum;
	}

	public void addParam(String param){
		params.add(param);
	}
}
