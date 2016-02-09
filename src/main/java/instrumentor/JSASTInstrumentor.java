package instrumentor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;


/**
 * This class is used to visit AST nodes of the given JS code. When a node matches a certain condition, it will be instrumented with a wrapper function.
 */

public class JSASTInstrumentor implements NodeVisitor{

	private int instrumentedLinesCounter = 0;

	private CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
	private String scopeName = null;	// Contains the scopename of the AST we are visiting. Generally this will be the filename
	protected String jsName = null;		//To store js corresponding name

	private int currentTestNumber = 0;

	private String currentTest = "";

	private int testCounter = 0;
	private int asynchTestCounter = 0;
	private int assertionCounter = 0;
	private int newExpressionCounter = 0;
	private int triggerCounetr = 0;
	
	public JSASTInstrumentor(){
	}

	/**
	 * @param scopeName
	 *            the scopeName to set
	 */
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
		//This is used to name the array which stores execution count for the scope in URL 
		int index = scopeName.lastIndexOf('/');
		String s = scopeName.substring(index+1, scopeName.length());
		jsName = s.replace('.', '_');
	}

	/**
	 * @return the scopeName
	 */
	public String getScopeName() {
		return scopeName;
	}

	/**
	 * Parse some JavaScript to a simple AST.
	 * 
	 * @param code
	 *            The JavaScript source code to parse.
	 * @return The AST node.
	 */
	public AstNode parse(String code) {
		//Parser p = new Parser(compilerEnvirons, null);

		compilerEnvirons.setErrorReporter(new ConsoleErrorReporter());
		Parser p = new Parser(compilerEnvirons, new ConsoleErrorReporter());

		//System.out.print(code+"*******\n");
		return p.parse(code, null, 0);
	}

	/**
	 * Find out the function name of a certain node and return "anonymous" if it's an anonymous function.
	 * 
	 * @param f
	 *            The function node.
	 * @return The function name.
	 */
	protected String getFunctionName(FunctionNode f) {
		if (f==null)
			return "NoFunctionNode";
		else if(f.getParent() instanceof ObjectProperty){
			return ((ObjectProperty)f.getParent()).getLeft().toSource();
		}
		Name functionName = f.getFunctionName();

		if (functionName == null) {
			return "anonymous" + f.getLineno();
		} else {
			return functionName.toSource();
		}
	}

	/**
	 * Create a new block node with two children.
	 * 
	 * @param node
	 *            The child.
	 * @return The new block.
	 */
	private Block createBlockWithNode(AstNode node) {
		Block b = new Block();
		b.addChild(node);
		return b;
	}


	/**
	 * Actual visiting method.
	 * 
	 * @param node
	 *            The node that is currently visited.
	 * @return Whether to visit the children.
	 */
	public boolean visit(AstNode node) {	
		//System.out.println("visit");
		String nodeName = node.shortName();
		int nodeType = node.getType();
		int nodeDepth = node.depth();
		/*
		System.out.println("node.shortName() : " + nodeName);
		System.out.println("node.depth() : " + nodeDepth);
		System.out.println("node.getLineno() : " + (node.getLineno()+1));
		System.out.println("node.toSource() : \n" + node.toSource());
		System.out.println("node.getType() : " + node.getType());
		System.out.println("node.debugPrint() : \n" + node.debugPrint());
		 */

		if (node instanceof NewExpression)
			newExpressionCounter++;
		else if (node instanceof FunctionCall)
			instrumentFunctionCallNode(node);
		else if (node instanceof FunctionNode)
			instrumentFunctionNode(node);

		/* have a look at the children of this node */
		return true;
	}


	private void instrumentFunctionNode(AstNode node) {
		FunctionNode f = (FunctionNode) node;
		int numOfParam = f.getParams().size();
		int lineNumber = node.getLineno()+1;
		int fLength = f.getEndLineno() - f.getLineno();
		int fDepth = node.depth();
		//System.out.println(f.debugPrint());
		/*for (Symbol s: f.getSymbols()){
			int sType = s.getDeclType();
			if (sType == Token.LP || sType == Token.VAR || sType == Token.LET || sType == Token.CONST){
				System.out.println("s.getName() : " + s.getName());
			}
		}*/
		//System.out.println(f.getSymbolTable());
		//System.out.println(f.getSymbols());

		System.out.println("=== instrumentFunctionNode ===");
		String fName = "";
		if (f.getFunctionName()!=null){
			fName = f.getFunctionName().getIdentifier();
			System.out.println("fName = " + fName);
		}
		System.out.println("Nothing instrumented!");

	}

	private void instrumentFunctionCallNode(AstNode node) {
		System.out.println("=== instrumentFunctionCallNode ===");
		// getting the enclosing function name
		String enclosingFunction = "";
		if (node.getEnclosingFunction()!=null)
			if (node.getEnclosingFunction().getFunctionName()!=null)
				enclosingFunction = ((FunctionNode) node.getEnclosingFunction()).getFunctionName().getIdentifier();

		System.out.println("enclosingFunction: " + enclosingFunction);

		if (node.shortName().equals("NewExpression"))
			return;

		FunctionCall fcall = (FunctionCall) node;
		AstNode targetNode = fcall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)

		// avoid instrumenting wrapper function calls!
		if (fcall.getParent().toSource().contains("funcionCallWrapper")){
			System.out.println("Not instrumenting " + fcall.getTarget().toSource() + ", because of: " + fcall.getParent().toSource());
			return;
		}

		String functionName = targetNode.toSource().substring(targetNode.toSource().lastIndexOf(".")+1);

		if (targetNode.toSource().equals("QUnit.module") || targetNode.toSource().equals("module"))
			currentTest  = "TestModule";
		if (targetNode.toSource().equals("QUnit.test") || targetNode.toSource().equals("test")){ 
			currentTestNumber++;
			currentTest = "Test" + Integer.toString(currentTestNumber);
			setTestCounter(getTestCounter() + 1);
		}
		if (targetNode.toSource().equals("QUnit.asyncTest()") || targetNode.toSource().equals("asyncTest()")){
			currentTestNumber++;
			currentTest = "AsynchTest" + Integer.toString(currentTestNumber);
			setAsynchTestCounter(getAsynchTestCounter() + 1);
		}

		if (targetNode.toSource().equals("trigger") || targetNode.toSource().equals("triggerHandler"))
			setTriggerCounetr(getTriggerCounetr() + 1);
		
		String[] assertionSkipList = { "assert.expect", "expect", "assert.equal", "equal", "assert.notEqual", "notEqual", "assert.deepEqual", "deepEqual", 
				"assert.notDeepEqual", "notDeepEqual", "assert.strictEqual", "strictEqual", "assert.notStrictEqual", "notStrictEqual", "QUnit.ok", "assert.ok", "ok", "assert.notOk", "notOk", 
				"assert.propEqual", "propEqual", "assert.notPropEqual", "notPropEqual", "assert.push", "assert.throws", "throws", "assert.async"};		

		String[] otherSkipList = { "QUnit.module", "module", "QUnit.test", "test", "QUnit.asyncTest", "asyncTest", "jQuery", "$" };		

		if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() ))
			setAssertionCounter(getAssertionCounter() + 1);
			
		if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() ) || ArrayUtils.contains( otherSkipList, targetNode.toSource() )) {
			System.out.println("Not instrumenting " + targetNode.toSource());
			return;
		}else
			System.out.println("Instrumenting " + functionName);

		// functionName, enclosingFunction, function
		List<AstNode> args = new ArrayList<AstNode>(fcall.getArguments());
		String wrapperCode = scopeName.replace(".js","").replace("-", "_") + "_funcionCallWrapper(\""+ functionName +"\", \"" + scopeName.replace(".js","") + "_" + currentTest + "\", " + fcall.toSource() + ")";
		System.out.println("wrapperCode : " + wrapperCode );

		AstNode wrapperNode = parse(wrapperCode);
		ExpressionStatement es = (ExpressionStatement)((AstNode) wrapperNode.getFirstChild());
		FunctionCall wrapperFunCall = (FunctionCall) es.getExpression();
		args.add(wrapperFunCall.getArguments().get(0));
		//wrapperFunCall.addArgument(fcall);
		System.out.println("Replacing functionCall: " + fcall.toSource() + " with wrapperFunCall: " + wrapperFunCall.toSource());
		fcall.setTarget(wrapperFunCall.getTarget());
		fcall.setArguments(wrapperFunCall.getArguments());			
		System.out.println("New functionCall: " + fcall.toSource());

	}

	/**
	 * Creates a node that can be inserted at a certain point in the AST root.
	 * 
	 * @param root
	 * 			The AST root that will enclose the node.
	 * @param postfix
	 * 			The postfix name.
	 * @param lineNo
	 * 			Linenumber where the node will be inserted.
	 * @param rootCount
	 * 			Unique integer that identifies the AstRoot
	 * @return The new node
	 */
	protected AstNode createNode(AstRoot root, String postfix, int lineNo, int rootCount) {
		// instrumenting out of function
		// Adds instrumentation code
		String code = jsName + "_exec_counter[" + Integer.toString(instrumentedLinesCounter) + "]++;";
		instrumentedLinesCounter++;

		return parse(code);
	}

	protected AstNode createFunctionTypeNameTrackingNode(FunctionNode callerFunc, AstNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  create node for logging variable/function-parameters
	 */
	protected AstNode createNode(FunctionNode function, AstNode nodeForVarLog, String statementCategory) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * create node for tracking function calls
	 */
	protected AstNode createFunctionTrackingNode(FunctionNode calleeFunction, String callerName) {
		return null;
	}	

	/**
	 * This method is called when the complete AST has been traversed.
	 * 
	 * @param node
	 *            The AST root node.
	 */
	public void finish(AstRoot node) {
		// add header code
		node.addChildToFront(headerCode());
		// instrumentedLinesCounter resets to 0 for the next codes
		instrumentedLinesCounter = 0;
	}

	/**
	 * This method is called before the AST is going to be traversed.
	 */
	public void start() {
		// just to be sure that index start from 0
		instrumentedLinesCounter = 0;
	}


	/**
	 * This will be added to the beginning of the script
	 * 
	 * @return The AstNode which contains array.
	 */
	private AstNode headerCode() {
		// statement can be functionCall, assignment, return, condition, etc.
		String code = scopeName.replace(".js","").replace("-", "_") + "_funcionCallTrace = [];";

		code += "function " + scopeName.replace(".js","").replace("-", "_") + "_funcionCallWrapper(functionName, testFunction, functionBody){" +
				scopeName.replace(".js","").replace("-", "_") +"_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});" +
				"return functionBody;" +
				"}";

		code += "function " + scopeName.replace(".js","").replace("-", "_") + "_getFuncionCallTrace(){" +
				"return " + scopeName.replace(".js","").replace("-", "_") + "_funcionCallTrace;" +
				"}";


		instrumentedLinesCounter = 0;

		return parse(code);
	}

	/**
	 * Creates a node that can be inserted at a certain point within a function.
	 * 
	 * @param function
	 *            The function that will enclose the node.
	 * @param postfix
	 *            The postfix function name (enter/exit).
	 * @param lineNo
	 *            Linenumber where the node will be inserted.
	 * @return The new node.
	 */
	protected AstNode createNode(FunctionNode function, String postfix, int lineNo) {
		String name = getFunctionName(function);

		// Adds instrumentation code
		String code = jsName + "_exec_counter[" + Integer.toString(instrumentedLinesCounter) + "]++;";
		instrumentedLinesCounter++;

		return parse(code);
	}

	public void resetUnitTestCounter() {
		currentTestNumber = 0;		
	}

	public int getAssertionCounter() {
		return assertionCounter;
	}

	public void setAssertionCounter(int assertionCounter) {
		this.assertionCounter = assertionCounter;
	}

	public int getNewExpressionCounter() {
		return newExpressionCounter;
	}

	public void setNewExpressionCounter(int newExpressionCounter) {
		this.newExpressionCounter = newExpressionCounter;
	}

	public int getTestCounter() {
		return testCounter;
	}

	public void setTestCounter(int testCounter) {
		this.testCounter = testCounter;
	}

	public int getAsynchTestCounter() {
		return asynchTestCounter;
	}

	public void setAsynchTestCounter(int asynchTestCounter) {
		this.asynchTestCounter = asynchTestCounter;
	}

	public int getTriggerCounetr() {
		return triggerCounetr;
	}

	public void setTriggerCounetr(int triggerCounetr) {
		this.triggerCounetr = triggerCounetr;
	}


}

