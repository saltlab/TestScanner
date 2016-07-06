package instrumentor;

import org.mozilla.javascript.ast.FunctionNode;

public class DOMVariableInfo {
	private String varName = "";				// e = getElementById("x");
	private int varLineNumber = 0;				
	private String functionName = "";		// the enclosing function name
	private int functionBeginLineNum = 0;	// the enclosing function begin line number
	private int functionEndLineNum = 0;	// the enclosing function begin line number

	public DOMVariableInfo(String varName, int varLineNumber, FunctionNode functionNode){
		this.varName = varName;
		this.varLineNumber = varLineNumber;
		int numOfParam = functionNode.getParams().size();
		this.functionBeginLineNum = functionNode.getLineno()+1;
		this.functionEndLineNum = functionNode.getEndLineno() - functionNode.getLineno();
		if (functionNode.getFunctionName()!=null){
			this.functionName = functionNode.getFunctionName().getIdentifier();
			//System.out.println("fName = " + this.functionName);
		}
	}

	public DOMVariableInfo(String varName, int functionBeginLineNumber, int functionEndLineNumber){
		this.varName = varName;
		this.varLineNumber = functionBeginLineNumber;
		this.functionBeginLineNum = functionBeginLineNumber;
		this.functionEndLineNum = functionEndLineNumber;
	}
	
	public boolean isUsedInForwardSlice(String var, int lineNumber){
		if (varName.equals(var) && lineNumber>=varLineNumber && lineNumber<=functionEndLineNum){
			//System.out.println("Var " + var + " in line " + lineNumber + " is used as DOM related...");
			return true;
		}
		//System.out.println("Var " + var + " in line " + lineNumber + " is is NOT used as DOM related!");
		return false;
	}	
}
